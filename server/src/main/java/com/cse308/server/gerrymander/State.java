/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.gerrymander;

import com.cse308.server.gerrymander.enums.Demographic;
import com.cse308.server.gerrymander.enums.StateName;
import com.cse308.server.gerrymander.result.DistrictInfo;
import com.cse308.server.gerrymander.result.VoteBlocResult;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Maverick
 */
public class State {
    int id;
    StateName stateName;
    int population;
    Set<Cluster> clusters;
    Set<Precinct> precincts;
    Map<Cluster,Cluster> mmPairs;
    
    public VoteBlocResult[] findVoteBlocs(float blocThreshold, float voteThreshold){
        return null;
    }
    
    public DistrictInfo getDistrictInfo(int districtId, Demographic[] demographic){
        return null;
    }
    
    public Map<Cluster,Cluster> setMMPairs(float minRange, float maxRange, Demographic[] demographics){
        return null;
    }
}
