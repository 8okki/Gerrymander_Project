/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.gerrymander;

import com.cse308.server.gerrymander.enums.DEMOGRAPHIC;
import com.cse308.server.gerrymander.enums.STATENAME;
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
    STATENAME stateName;
    int population;
    Set<Cluster> clusters;
    Set<Precinct> precincts;
    Map<Cluster,Cluster> mmPairs;
    
    public VoteBlocResult[] findVoteBlocs(float blocThreshold, float voteThreshold){
        return null;
    }
    
    public DistrictInfo getDistrictInfo(int districtId, DEMOGRAPHIC[] demographic){
        return null;
    }
    
    public Map<Cluster,Cluster> setMMPairs(float minRange, float maxRange, DEMOGRAPHIC[] demographics){
        return null;
    }
}
