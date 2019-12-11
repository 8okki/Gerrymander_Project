/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.models;

import com.cse308.server.algorithm.Move;
import com.cse308.server.enums.Demographic;
import com.cse308.server.measure.MeasureFunction;
import com.cse308.server.result.DistrictInfo;
import com.cse308.server.result.VoteBlocResult;

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
    private Set<Cluster> clusters;

    @Transient
    private Map<Cluster,Cluster> pairs;

    private MeasureFunction clusterScoreFunction;
    private Map<Cluster, Double> clusterScores;


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

    public void setName(String name) {
        this.name = name;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public void setPrecincts(Set precincts) {
        this.precincts = precincts;
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
        this.clusters = new HashSet<>();
        for(Precinct precinct : this.precincts)
            this.clusters.add(new Cluster(this, precinct));

        for(Cluster cluster : clusters){
            Precinct precinct = (Precinct) cluster.getPrecincts().toArray()[0];
            for(Precinct neighbor : precinct.getNeighbors())
                cluster.getAdjacentClusters().add(neighbor.getCurrentCluster());
        }
    }

    public void setMMPairs(float minRange, float maxRange, List<Demographic> demographics) {
        pairs = new HashMap<>();
        for (Cluster cluster : clusters) {
            if (!pairs.containsKey(cluster)) {
                Cluster pair = cluster.findMMPair(minRange, maxRange, demographics);
                if (pair != null) {
                    pairs.put(cluster, pair);
                    pairs.put(pair, cluster);
                }
            }
        }
        for(Cluster pairedCluster : pairs.keySet()){
            clusters.remove(pairedCluster);
        }
    }

    public void setPairs(float targetPopulation) {
        for(Cluster cluster : clusters) {
            if (!pairs.containsKey(cluster)) {
                Cluster pair = cluster.findPair(targetPopulation);
                if (pair != null) {
                    pairs.put(cluster, pair);
                    pairs.put(pair, cluster);
                }
            }
        }
        for(Cluster pairedCluster : pairs.keySet()){
            clusters.remove(pairedCluster);
        }
    }

    public void mergePairs() {
        // If no pairs are pre-made, manually make one pair based on population
        if(pairs.isEmpty()) {
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
        }

        // Merge all pairs
        for (Cluster cluster : pairs.keySet()) {
            if (!cluster.isMerged()) {
                cluster.merge(pairs.get(cluster));
                clusters.add(cluster);
            }
        }
    }


    /* Phase 2 */
    public void initClusterScores() {
        clusterScores = new HashMap<>();
        for (Cluster cluster : clusters) {
            double score = clusterScoreFunction.calculateMeasure(cluster);
            clusterScores.put(cluster, score);
        }
    }

    public double anneal() {
        double prevScore = 0, newScore = 0;

        while (!isStagnant(prevScore, newScore)) {
            prevScore = newScore;
            Cluster worstCluster = getLowestScoreCluster();
            worstCluster.anneal();
            newScore = objectiveFunction();
        }

        return newScore;
    }

    public Cluster getLowestScoreCluster() {
        Cluster worstCluster = null;
        double minScore = Double.POSITIVE_INFINITY;

        for (Cluster cluster : clusters) {
            double score = clusterScores.get(cluster);
            if (score < minScore) {
                worstCluster = cluster;
                minScore = score;
            }
        }

        return worstCluster;
    }

    public double objectiveFunction() {
        double score = 0;

        for (Cluster cluster : clusters)
            score += clusterScores.get(cluster);

        return score;
    }

    public boolean isStagnant(double prevScore, double newScore) {
        return prevScore == newScore;
    }


    @Override
    public String toString(){
        return "[Name: " + this.name.toString() +
                ", population: " + this.population + "]";
    }
}
