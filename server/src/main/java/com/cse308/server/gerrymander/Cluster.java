/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.gerrymander;

import com.cse308.server.gerrymander.enums.Demographic;
import com.cse308.server.gerrymander.result.DistrictInfo;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Mavericks
 */
public class Cluster {
    
    int id;
    int population;
    Set<Precinct> precincts;
    Map<Demographic, Integer> demographicPopDist;
    Set<Cluster> adjClusters;
    
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
        return null;
    }
    public Map<Cluster, Cluster> findMMPair(float minRange, float maxRange, Demographic[] demographic){
        return null;
    }
    
    public int getDemographicPopSum(Demographic[] demographics){
        return -1;
    }
    
    public DistrictInfo getDistrictInfo(int statePopulation, Demographic[] demographics){
        return null;
    }
    
    private float calculateRatio(int demographicPopSum, int populationSum){
        return (float)demographicPopSum/populationSum;
    }
    
    private boolean checkPair(Cluster cluster2, float ratio){
        return false;
    }
    
}
