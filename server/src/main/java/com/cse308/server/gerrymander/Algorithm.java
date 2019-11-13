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

/**
 *
 * @author Mavericks
 */

public class Algorithm {
    
    Map<String, State> states;
    
    public State initState(){
        return null;
    }
    
    public VoteBlocResult[] runPhase0(StateName statename, float blocThreshold, float voteThreshold){
        return null;
    }
    
    public DistrictInfo getDistrictInfo(StateName stateName, int districtId, Demographic[] demographics){
        return null;
    }
}
