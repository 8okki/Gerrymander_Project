/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.algorithm;

import com.cse308.server.enums.Demographic;
import com.cse308.server.enums.StateName;
import com.cse308.server.result.DistrictInfo;
import com.cse308.server.result.VoteBlocResult;
import com.cse308.server.hibernate.dao.StateDao;
import java.util.List;

import com.cse308.server.models.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Mavericks
 */
@Controller
public class Algorithm {
    @Autowired
    StateDao stateDao;
    
    State state;
    
    public State initState(StateName stateName){
        if(this.state == null || StateName.valueOf(this.state.getName()) != stateName){
            State result = stateDao.getStateById(stateName.name());
            if(result != null){
                this.state = result;
                return this.state;
            }else{
                return null;
            }    
        }else{
            return this.state;
        }
    }

    public void initGeometry(StateName stateName) {

    }

    public List<VoteBlocResult> runPhase0(float blocThreshold, float voteThreshold){
        return this.state.findVoteBlocs(blocThreshold, voteThreshold);
    }
    
    public void runPhase1(List<Demographic> demographics, float demographicMinimum, float demographicMaximum, int targetDistrictNum){
        this.state.initClusters();
        float targetPopulation = (float) this.state.getPopulation() / targetDistrictNum;

        while(this.state.getClusters().size() < targetDistrictNum) {
            this.state.setMMPairs(demographicMinimum, demographicMaximum, demographics);
            this.state.setPairs(targetPopulation);
            this.state.mergePairs();
        }
    }

    public void runPhase2(){

    }

    public DistrictInfo getDistrictInfo(int districtId, Demographic[] demographics){
        return this.state.getDistrictInfo(districtId, demographics);
    }
}
