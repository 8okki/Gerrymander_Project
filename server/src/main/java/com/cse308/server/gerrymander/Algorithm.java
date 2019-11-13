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

/**
 *
 * @author Jakob
 */

public class Algorithm {
    Map<String, State> states;
    
    public State initState(){
        return null;
    }
    
    public VoteBlocResult[] runPhase0(STATENAME statename, float blocThreshold, float voteThreshold){
        return null;
    }
    
    public DistrictInfo getDistrictInfo(STATENAME stateName, int districtId, DEMOGRAPHIC[] demographics){
        return null;
    }
}
