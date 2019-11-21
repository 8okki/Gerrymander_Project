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
import com.cse308.server.hibernate.dao.StateDao;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Mavericks
 */

public class Algorithm {
    StateDao stateDao = new StateDao();
    State state;
    
    public State initState(StateName stateName){
        List<State> results = stateDao.getStateById(stateName);
        if(!results.isEmpty()){
            this.state = results.get(0);
            return this.state;
        }else{
            return null;
        }    
    }
    
    public List<VoteBlocResult> runPhase0(float blocThreshold, float voteThreshold){
        return this.state.findVoteBlocs(blocThreshold, voteThreshold);
    }
    
    public void runPhase1(float demographicMinimum, float demographicMaximum, List<Demographic> demographics){
        state.initClusters();
        state.setMMPairs(demographicMinimum, demographicMaximum, demographics);

    }
    
    public DistrictInfo getDistrictInfo(int districtId, Demographic[] demographics){
        return this.state.getDistrictInfo(districtId, demographics);
    }
}
