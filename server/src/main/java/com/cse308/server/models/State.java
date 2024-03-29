/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.models;

import com.cse308.server.algorithm.Move;
import com.cse308.server.enums.Demographic;
import com.cse308.server.measure.ObjectiveFunction;
import com.cse308.server.measure.Measure;
import com.cse308.server.measure.MeasureFunction;
import com.cse308.server.result.*;

import java.util.*;
import javax.persistence.*;

import static java.lang.System.nanoTime;

/**
 *
 * @author Mavericks
 */

@NamedQueries({
        @NamedQuery(name = "State_findByName",
                query = "from State where id = :ID")
})

/*@NamedNativeQueries({
        @NamedNativeQuery(name = "State_findByName", query = "select * from states where name = :NAME", resultClass = State.class)
})*/

@Entity
@Table(name="states")
public class State {
    @Id
    private String name;
    private int population;
    
    @OneToMany(mappedBy="state",fetch=FetchType.LAZY)
    private Set<Precinct> precincts;

    @Transient
    private boolean neighborsLoaded;
    
    @Transient
    private boolean geometryLoaded;
    
    @Transient
    private Set<Cluster> clusters;

    @Transient
    private Map<Cluster,Cluster> pairs;

    @Transient
    private ObjectiveFunction objFunction;

    /* Getters & Setters */
    public String getName(){
        return this.name.toString();
    }

    public int getPopulation(){
        return this.population;
    }

    public DistrictInfo getDistrictInfo(int districtId, Demographic[] demographic){
        return null;
    }

    public Set<Cluster> getClusters() {
        return clusters;
    }

    public Set<Precinct> getPrecincts() {
        return this.precincts;
    }

    public boolean areNeighborsLoaded(){
        return this.neighborsLoaded;
    }
    
    public boolean isGeometryLoaded(){
        return this.geometryLoaded;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public void setPrecincts(Set precincts) {
        this.precincts = precincts;
    }
    
    public void setNeighborsLoaded(boolean neighborsLoaded){
        this.neighborsLoaded = neighborsLoaded;
    }
    
    public void setGeometryLoaded(boolean geometryLoaded){
        this.geometryLoaded = geometryLoaded;
    }

    public void setObjFunction(ObjectiveFunction function) { this.objFunction = function; }

    /* Constructor */
    public State() {}


    /* Phase 0 */
    public List<VoteBlocResult> findVoteBlocs(float blocThreshold, float voteThreshold){
        List<VoteBlocResult> voteBlocResults = new ArrayList<>();
        for(Precinct precinct : this.precincts){
            VoteBlocResult result = precinct.findVoteBloc(blocThreshold, voteThreshold);
            if(result != null){
                voteBlocResults.add(result);
            }
        }
        return voteBlocResults;
    }


    /* Phase 1 */
    public void initClusters(float min, float max, List<Demographic> demoMM){
        Comparator<Cluster> comparator = Comparator.comparing(Cluster::getPopulation).thenComparing(Cluster::getId);
        clusters = new TreeSet<>(comparator);
        int id = 1;
        for(Precinct precinct : precincts){
            if(precinct.getCode().equals("123-ACQ") || precinct.getCode().equals("043-ACN"))
                continue;
            clusters.add(new Cluster(id++,this, precinct, min, max, demoMM));
        }

        for(Cluster cluster : clusters){
            Precinct precinct = (Precinct) cluster.getPrecincts().toArray()[0];
            for(Precinct neighbor : precinct.getNeighbors())
                cluster.getAdjacentClusters().add(neighbor.getCurrentCluster());
        }

    }

    public void resetPairs() {
        this.pairs = new HashMap<>();
        for (Cluster c : this.clusters){
            c.setIsMerged(false);
        }
    }

    public void makeMMPairs(float idealPopulation) {
        for (Cluster cluster : clusters) {
            if (!cluster.isMerged()) {
                Cluster pair = cluster.findMMPair(idealPopulation);
                if (pair != null) {
                    pairs.put(cluster, pair);
                    cluster.setIsMerged(true);
                    pair.setIsMerged(true);
                    System.out.println("MM Made");
                }
            }
        }
    }

    public void makePairs(float idealPopulation) {
        for(Cluster cluster : clusters) {
            if (!cluster.isMerged()) {
                Cluster pair = cluster.findPair(idealPopulation);
                if (pair != null) {
                    pairs.put(cluster, pair);
                    cluster.setIsMerged(true);
                    pair.setIsMerged(true);
                    System.out.println("No-MM Made");
                }
            }
        }
    }

    public void makeRandomPair() {
        Cluster cluster = ((TreeSet<Cluster>)clusters).first();
        Cluster neighbor = getRandom(cluster.getAdjacentClusters());
        pairs.put(cluster, neighbor);
        cluster.setIsMerged(true);
        neighbor.setIsMerged(true);
    }

    public void makeManualPair(float idealPopulation){
        for(Cluster cluster : clusters){
            Cluster neighbor = getRandom(cluster.getAdjacentClusters());
            if(cluster.isPair(neighbor, idealPopulation)){
                pairs.put(cluster, neighbor);
                cluster.setIsMerged(true);
                neighbor.setIsMerged(true);
                System.out.println("Manual Made");
                break;
            }
        }
    }

    public void mergePairs(int targetDistrictNum) {
        // If no pairs are pre-made, manually make one pair based on population
        if(pairs.isEmpty())
            makeRandomPair();

        // Merge all pairs
        for (Cluster cluster : pairs.keySet()) {
            if (cluster.isMerged() && pairs.get(cluster).isMerged()){
                cluster.merge(pairs.get(cluster));
                clusters.add(cluster);
            }
            if(clusters.size() == targetDistrictNum){
                return;
            }
        }
    }


    /* Phase 2 */
    public Phase2Result anneal(List<Measure> measures) {
        // Initialize score function
        objFunction = new ObjectiveFunction(measures);

        // Initialize scores & mmCounts
        double initialObj, prevObj, newObj;
        int initialMM, prevMM, newMM;
        initialObj = prevObj = newObj= objFunction.calculateObj(clusters);
        initialMM = prevMM = newMM = countMM();
        Map<String, Double[]> scores = new HashMap<>();
        for(Measure measure : measures){
            Double[] score = {measure.getScore(), measure.getScore()};
            scores.put(measure.name(), score);
        }

        // Initialize Stagnation Counter
        final int MAX_STAG = 100;
        final long TIME_LIMIT = 2;
        int stagnation = 0;

        // Anneal randomly until converges or passes time limit
        int elapsedTime = 0;
        long startTime = System.nanoTime();
        while (stagnation <= MAX_STAG && elapsedTime < TIME_LIMIT) {
            Cluster cluster = getRandom(clusters);
            if(cluster == null)
                break;

            Move move = cluster.findRandomMove();
            if (move != null){
                move.execute();
                newMM = countMM();
                newObj = objFunction.calculateObj(clusters);
                if (newMM < prevMM || newObj < prevObj) {
                    move.undo();
                    newObj = prevObj;
                    newMM = prevMM;
                } else
                    for (Measure measure : measures)
                        scores.get(measure.name())[1] = measure.getScore();
            }

            System.out.println(newObj);

            stagnation = isStagnant(prevObj, newObj) ? stagnation+1 : 0;

            prevMM = newMM;
            prevObj = newObj;
            elapsedTime = (int) ((System.nanoTime() - startTime) / 1e+9);
        }

        int[] MM = {initialMM, newMM};
        double[] objs = {initialObj, newObj};
        Phase2Result result = new Phase2Result(MM, objs, scores, createDistrictResults());
        System.out.println("-----------------------Anneal Finished-----------------------");
        return result;
    }

    public boolean isStagnant(double prevScore, double newScore){
        return Math.abs(prevScore - newScore) < 0.00001;
    }

    public int countMM() {
        int mm = 0;
        for(Cluster cluster : clusters)
            if(cluster.isMM())
                mm++;
        return mm;
    }

    public <E> E getRandom(Set<E> set){
        if(set.isEmpty())
            return null;

        return set.stream().skip((int) (set.size() * Math.random())).findFirst().get();
    }

    public List<DistrictInfo> createDistrictResults(){
        List<DistrictInfo> results = new ArrayList<>();
        for(Cluster c : clusters){
            List<String> precinctCodes = new ArrayList<>();
            for(Precinct p : c.getPrecincts())
                precinctCodes.add(p.getCode());
            results.add(new DistrictInfo(c.getId(), precinctCodes, c.getDemographicPopDist(), c.getPopulation()));
        }
        return results;
    }

    @Override
    public String toString(){
        return "[Name: " + this.name + ", population: " + this.population + "]";
    }
}
