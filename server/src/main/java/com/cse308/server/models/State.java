/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.models;

import com.cse308.server.enums.Demographic;
import com.cse308.server.measure.MeasureFunction;
import com.cse308.server.result.DistrictInfo;
import com.cse308.server.result.VoteBlocResult;
import com.sun.xml.bind.v2.TODO;

import java.util.*;
import javax.persistence.*;

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
    private MeasureFunction clusterScoreFunction;


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

    public MeasureFunction getClusterScoreFunction() { return clusterScoreFunction; }
    
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

    public void setScoreFunction(MeasureFunction function) { this.clusterScoreFunction = function; }

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
    public void initClusters(){
        clusters = new HashSet<>();
        int id = 1;
        for(Precinct precinct : precincts){
            clusters.add(new Cluster(id++,this, precinct));
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

    public void setMMPairs(float minRange, float maxRange, List<Demographic> demographics) {
        for (Cluster cluster : clusters) {
            if (!cluster.isMerged()) {
                Cluster pair = cluster.findMMPair(minRange, maxRange, demographics);
                if (pair != null) {
//                    System.out.println("mm pair found");
                    pairs.put(cluster, pair);
                    cluster.setIsMerged(true);
                    pair.setIsMerged(true);
                }
            }
        }
    }

    public void setPairs(float targetPopulation) {
        for(Cluster cluster : clusters) {
            if (!cluster.isMerged()) {
                Cluster pair = cluster.findPair(targetPopulation);
                if (pair != null) {
//                    System.out.println("reg pair found");
                    pairs.put(cluster, pair);
                    cluster.setIsMerged(true);
                    pair.setIsMerged(true);
                }
            }
        }
    }

    public void makeRandomPair() {
        int c = (int) (Math.random() * clusters.size());
        int i = 0;
        for(Cluster cluster : clusters){
            if(i++ == c)
                for (Cluster neighbor : cluster.getAdjacentClusters()){
                    pairs.put(cluster, neighbor);
                    cluster.setIsMerged(true);
                    neighbor.setIsMerged(true);
                    return;
                }
        }
    }

    public void makeManualPair(){
        int currentMin = Integer.MAX_VALUE;
        Cluster[] minClusters = new Cluster[2];
        for (Cluster cluster : clusters) {
            for (Cluster neighbor : cluster.getAdjacentClusters()) {
                int sum = cluster.getPopulation() + neighbor.getPopulation();
                if (sum < currentMin) {
                    currentMin = sum;
                    minClusters[0] = cluster;
                    minClusters[1] = neighbor;
                }
            }
        }
        pairs.put(minClusters[0], minClusters[1]);
        minClusters[0].setIsMerged(true);
        minClusters[1].setIsMerged(true);
    }

    public void mergePairs(int targetDistrictNum) {
        // If no pairs are pre-made, manually make one pair based on population
        if(pairs.isEmpty())
            makeRandomPair();

        // Merge all pairs
        for (Cluster cluster : pairs.keySet()) {
            if (cluster.isMerged() && pairs.get(cluster).isMerged()) {
                cluster.merge(pairs.get(cluster));
                clusters.add(cluster);
            }
            if(clusters.size() == targetDistrictNum){
                return;
            }
        }
    }


    /* Phase 2 */
    public double[] anneal() {
        // Initialize scores
        double initialScore, prevScore, newScore;
        initialScore = prevScore = objectiveFunction();
        newScore = 0;

        // Anneal each cluster until converges
        int stag_count = 0;
        final int MAX_STAG = 5;
        while (stag_count <= MAX_STAG) {
            Cluster worstCluster = getLowestScoreCluster();
            newScore = worstCluster.anneal(prevScore);
            stag_count = isStagnant(prevScore, newScore) ? stag_count+1 : 0;
            prevScore = newScore;

            System.out.println(newScore);
        }

//        double finalScore = newScore > prevScore ? newScore : prevScore;
        double[] result = {initialScore, newScore};
        System.out.println("Anneal Finished");
        return result;
    }

    public Cluster getLowestScoreCluster() {
        Cluster worstCluster = null;
        double minScore = Double.POSITIVE_INFINITY;
        for (Cluster cluster : clusters) {
            if (cluster.getScore() < minScore) {
                worstCluster = cluster;
                minScore = cluster.getScore();
            }
        }
        return worstCluster;
    }

    public double objectiveFunction() {
        double score = 0;
        for (Cluster cluster : clusters){
            cluster.setScore(clusterScoreFunction.calculateMeasure(cluster));
            score += cluster.getScore();
        }
        return score;
    }


    public boolean isStagnant(double prevScore, double newScore){
        return Math.abs(prevScore - newScore) < 0.0001;
    }

    @Override
    public String toString(){
        return "[Name: " + this.name.toString() +
                ", population: " + this.population + "]";
    }
}
