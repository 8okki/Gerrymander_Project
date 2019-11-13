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
        return null;
    }
    
    private Demographic findLargestDemographic(){
        return null;
    }
    
    private float calculateRatio(int largestDemographicPop, int totalPop){
        return (float)largestDemographicPop/totalPop;
    }
}
