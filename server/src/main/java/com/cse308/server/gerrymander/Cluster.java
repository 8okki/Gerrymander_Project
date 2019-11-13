/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.gerrymander;

/**
 *
 * @author Mavericks
 */
public class Cluster {
    
    int id;
    int population;
    Set<Precinct> precincts;
    Map<DEMOGRAPHIC, int> demographicPopDist;
    Set<Cluster> adjClusters;
    
    public int getPopulation(){
        return this.population;
    }
    
    public Set<Cluster> getAdjacentClusters(){
        return this.adjClusters;
    }
    
    public Map<DEMOGRAPHIC, int> getDemographicPopDist(){
        return this.demographicPopDist;
    }
    
    public Map<DEMOGRAPHIC, int> getDemographicPopDist(DEMOGRAPHIC[] demographics){
        return null;
    }
    public Map<Cluster, Cluster> findMMPair(float minRange, float maxRange, DEMOGRAPHIC[] demographic){
        return null;
    }
    
    public int getDemographicPopSum(DEMOGRAPHIC[] demographics){
        return null;
    }
    
    public DistrictInfo getDistrictInfo(int statePopulation, DEMOGRAPHIC[] demographics){
        return null;
    }
    
    private float calculateRatio(int demographicPopSum, int populationSum){
        return (float)demographicPopSum/populationSum;
    }
    
    private boolean checkPair(Cluster cluster2, float ratio){
        return null;
    }
    
}
