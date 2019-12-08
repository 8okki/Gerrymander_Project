/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.models;

import com.cse308.server.enums.Demographic;
import com.cse308.server.result.DistrictInfo;

import java.util.*;

/**
 *
 * @author Mavericks
 */
public class Cluster {
    int population;
    Set<Precinct> precincts;
    Map<Demographic, Integer> demographicPopDist;
    Set<Cluster> adjClusters;
    boolean isMerged;


    public int getPopulation(){
        return this.population;
    }
    
    public Set<Cluster> getAdjacentClusters(){
        return this.adjClusters;
    }
    
    public Map<Demographic, Integer> getDemographicPopDist(){
        return this.demographicPopDist;
    }
    
    public Map<Demographic, Integer> getDemographicPopDist(Demographic[] demographics){
        HashMap<Demographic, Integer> output = new HashMap<Demographic, Integer>();
        for(int i = 0; i < demographics.length; i++){
            for(Map.Entry<Demographic, Integer> entry : this.demographicPopDist.entrySet()){
                if (demographics[i].equals(entry.getKey())){
                    output.put(entry.getKey(),entry.getValue());
                }
            }
        }
        return output;
    }

    public boolean isMerged() { return isMerged; }

    public void setIsMerged(boolean isMerged) { this.isMerged = isMerged; }

    public Cluster(Precinct precinct) {
        this.precincts = new HashSet<>();
        population = precinct.getPopulation();
        demographicPopDist = new HashMap<>();
        for(Demographic demographic : precinct.getDemographicPopDist().keySet()){
            demographicPopDist.put(demographic, precinct.getDemographicPopDist().get(demographic));
        }
        precincts.add(precinct);
        adjClusters = new HashSet<>();
    }

    public Cluster findMMPair(float minRange, float maxRange, List<Demographic> demographics){
        for(Cluster cluster : adjClusters) {
            if(!cluster.isMerged() && isMMPair(cluster, demographics, minRange, maxRange)) {
                return cluster;
            }
        }
        return null;
    }

    public Cluster findPair(float targetPopulation) {
        for(Cluster cluster : adjClusters) {
            if(!cluster.isMerged() && isPair(cluster, targetPopulation)){
                return cluster;
            }
        }
        return null;
    }

    public void merge(Cluster cluster) {
        this.population += cluster.getPopulation();
        for (Demographic demographic : demographicPopDist.keySet()){
            int sum = this.demographicPopDist.get(demographic) + cluster.getDemographicPopDist().get(demographic);
            demographicPopDist.put(demographic, sum);
        }
        this.precincts.addAll(cluster.getPrecincts());
        for(Cluster neighbor : cluster.getAdjacentClusters()){
            if(!this.adjClusters.contains(neighbor)){
                adjClusters.add(neighbor);
            }
        }
        cluster.setIsMerged(true);
    }

    public int getDemographicPopSum(List<Demographic> demographics){
        int sum = 0;
        for(Demographic demographic : demographics) {
            sum += this.demographicPopDist.get(demographic);
        }
        return sum;
    }
    
    public DistrictInfo getDistrictInfo(int statePopulation, Demographic[] demographics){
        return null;
    }

    public Set<Precinct> getPrecincts(){
        return this.precincts;
    }

    private static float calculateRatio(int demographicPopSum, int populationSum){
        return (float)demographicPopSum / populationSum;
    }
    
    private boolean isMMPair(Cluster cluster, List<Demographic> demographics, float minRange, float maxRange){
        int pairDemographicPopSum = this.getDemographicPopSum(demographics) + cluster.getDemographicPopSum(demographics);
        int pairTotalPopulation = this.getPopulation() + cluster.getPopulation();
        float ratio = calculateRatio(pairDemographicPopSum, pairTotalPopulation);
        return ratio >= minRange && ratio <= maxRange;
    }

    private boolean isPair(Cluster cluster, float targetPopulation) {
        int populationSum = this.getPopulation() + cluster.getPopulation();
        return populationSum <= targetPopulation;
    }

}
