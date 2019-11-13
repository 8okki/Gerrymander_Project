/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.gerrymander;

import com.cse308.server.gerrymander.enums.Demographic;
import com.cse308.server.gerrymander.result.VoteBlocResult;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

/**
 *
 * @author Maverick
 */
public class Precinct {
    
    Set<Precinct> neighbors;
    int population;
    Map<Demographic, Integer> demographicPopulationDist;
    Votes electionVotes;
    String name;
    
    public Demographic findDemographicBlocs(float threshold){
        return null;
    }
    
    public VoteBlocResult getDemographicPop(float blocThreshold, float voteThreshold){
        return null;
    }
    
    public Map<Demographic, Integer> getDemogrphicPop(Demographic[] demographics){
        HashMap<Demographic, Integer> output = new HashMap<Demographic, Integer>();
        for(int i = 0; i < demographics.length; i++){
            for(Map.Entry<Demographic, Integer> entry : this.demographicPopulationDist.entrySet()){
                if (demographics[i].equals(entry.getKey())){
                    output.put(entry.getKey(),entry.getValue());
                }
            }
        }
        return output;
    }
    
    private Demographic findLargestDemographic(){
        int curLargest = Integer.MAX_VALUE;
        Demographic curDemo = null;
        for(Map.Entry<Demographic, Integer> entry : this.demographicPopulationDist.entrySet()){
            if (entry.getValue() > curLargest){
                curLargest = entry.getValue();
                curDemo = entry.getKey();
            }
        }
        return curDemo;
    }
    
    private static float calculateRatio(int largestDemographicPop, int totalPop){
        return (float)largestDemographicPop/totalPop;
    }
}
