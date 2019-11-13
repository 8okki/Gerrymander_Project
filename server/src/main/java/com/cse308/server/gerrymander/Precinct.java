/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.gerrymander;

/**
 *
 * @author Maverick
 */
public class Precinct {
    
    Set<Precinct> neighbors;
    int population;
    Map<DEMOGRAPHIC, int> demographicPopulationDist;
    Votes electionVotes;
    String name;
    
    public DEMOGRAPHIC findDemographicBlocs(float threshold){
        return null;
    }
    
    public VoteBlocresult getDemographicPop(float blocThreshold, float voteThreshold){
        return null;
    }
    
    public Map<DEMOGRAPHIC, int> getDemogrphicPop(DEMOGRAPHIC[] demographics){
        return null;
    }
    
    private DEMOGRAPHIC findLargestDemographic(){
        return null;
    }
    
    private float calculateRatio(int largestDemographicPop, int totalPop){
        return (float)largestDemographicPop/totalPop;
    }
}
