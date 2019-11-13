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
import java.util.HashMap;

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

    public Map<Cluster, Cluster> findMMPair(float minRange, float maxRange, Demographic[] demographic){
        return null;
    }
    
    public int getDemographicPopSum(Demographic[] demographics){
        int sum = 0;
        for(int i = 0; i < demographics.length; i++){
            for(Map.Entry<Demographic, Integer> entry : this.demographicPopDist.entrySet()){
                if (demographics[i].equals(entry.getKey())){
                    sum += entry.getValue();
                }
            }
        }
        return sum;
    }
    
    public DistrictInfo getDistrictInfo(int statePopulation, Demographic[] demographics){
        return null;
    }
    
    private static float calculateRatio(int demographicPopSum, int populationSum){
        return (float)demographicPopSum/populationSum;
    }
    
    private boolean checkPair(Cluster cluster2, float ratio){
        return false;
    }
    
}
