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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Mavericks
 */

public class Algorithm {
    StateDao stateDao = new StateDao();
    State state;
    
    public State initState(StateName stateName){
        if(this.state == null || StateName.valueOf(this.state.getName()) != stateName){
            List<State> results = stateDao.getStateById(stateName);
            if(!results.isEmpty()){
                this.state = results.get(0);
                return this.state;
            }else{
                return null;
            }    
        }else{
            return this.state;
        }
    }
    
    public List<VoteBlocResult> runPhase0(float blocThreshold, float voteThreshold){
        return this.state.findVoteBlocs(blocThreshold, voteThreshold);
    }
    
    public void runPhase1(float demographicMinimum, float demographicMaximum, List<Demographic> demographics, int targetDistrictNum){
        System.out.println("test");
        this.state.initClusters();
        System.out.println("helo");
        float targetPopulation = (float) this.state.getPopulation() / targetDistrictNum;

        while(this.state.getClusters().size() < targetDistrictNum) {
            this.state.setMMPairs(demographicMinimum, demographicMaximum, demographics);
            this.state.setPairs(targetPopulation);
            this.state.mergePairs();
        }
    }
    
    public DistrictInfo getDistrictInfo(int districtId, Demographic[] demographics){
        return this.state.getDistrictInfo(districtId, demographics);
    }
}
