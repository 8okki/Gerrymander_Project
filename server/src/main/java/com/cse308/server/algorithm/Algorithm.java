/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.algorithm;

import com.cse308.server.enums.Demographic;
import com.cse308.server.enums.StateName;
import com.cse308.server.measure.DefaultMeasure;
import com.cse308.server.measure.Measure;
import com.cse308.server.models.Cluster;
import com.cse308.server.models.Precinct;
import com.cse308.server.result.DistrictInfo;
import com.cse308.server.result.VoteBlocResult;
import com.cse308.server.hibernate.dao.StateDao;
import com.cse308.server.models.State;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.locationtech.jts.io.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.locationtech.jts.io.WKTReader;

/**
 *
 * @author Mavericks
 */
@Controller
public class Algorithm {
    @Autowired
    StateDao stateDao;
    
    State state;

    public DistrictInfo getDistrictInfo(int districtId, Demographic[] demographics){
        return this.state.getDistrictInfo(districtId, demographics);
    }

    /* Initialize */
    public State initState(StateName stateName){
        if(state == null || StateName.valueOf(state.getName()) != stateName){
            State result = stateDao.getStateById(stateName.name());
            if(result != null){
                state = result;
                return state;
            }else{
                return null;
            }    
        }else{
            return state;
        }
    }
    public void initGeometry() {
        try {
            Set<Precinct> precincts = state.getPrecincts();
            WKTReader reader = new WKTReader();
            for (Precinct precinct : precincts){
                String boundaryJson = precinct.getGeojson();
                precinct.setGeometry(reader.read(boundaryJson));
            }   
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /* Phase 0 */
    public List<VoteBlocResult> runPhase0(float blocThreshold, float voteThreshold){
        return this.state.findVoteBlocs(blocThreshold, voteThreshold);
    }

    /* Phase 1 */
    public void runPhase1(List<Demographic> demographics, float demographicMinimum, float demographicMaximum, int targetDistrictNum){
        this.state.initClusters();
        float targetPopulation = (float) this.state.getPopulation() / targetDistrictNum;

        while(this.state.getClusters().size() < targetDistrictNum) {
            this.state.setMMPairs(demographicMinimum, demographicMaximum, demographics);
            this.state.setPairs(targetPopulation);
            this.state.mergePairs();
        }
    }

    /* Phase 2 */
    public void runPhase2(List<Measure> measures){
        Map<Measure, Function<Cluster, Double>> weightFunctions = new HashMap<>();

        Function<Cluster, Double> weightFunction = new Function<Cluster, Double>() {
            @Override
            public Double apply(Cluster cluster) {
                return 1.0;
            }
        };
        for (Measure measure : measures)
            weightFunctions.put(measure, weightFunction);

        state.setScoreFunction(new DefaultMeasure(weightFunctions));
        state.initClusterScores();
        double score = state.anneal();
    }
}
