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
import com.cse308.server.result.*;
import com.cse308.server.hibernate.dao.StateDao;
import com.cse308.server.models.State;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.wololo.geojson.Feature;
import org.wololo.geojson.GeoJSONFactory;
import org.wololo.jts2geojson.GeoJSONReader;

/**
 *
 * @author Mavericks
 */
@Controller
public class Algorithm {
    @Autowired
    StateDao stateDao;
    
    State state;

    boolean incrementalRunning;
    
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
        if(state.isGeometryLoaded()){
            return;
        }
        state.setGeometryLoaded(true);
        Set<Precinct> precincts = state.getPrecincts();
        GeoJSONReader reader = new GeoJSONReader();
        for (Precinct precinct : precincts){
            Feature feature = (Feature) GeoJSONFactory.create(precinct.getGeojson());
            precinct.setGeometry(reader.read(feature.getGeometry()));
        }
        System.out.println("Geometry Loaded");
    }
    
    public void initNeighbors() {
        if(state.areNeighborsLoaded()){
            return;
        }
        state.setNeighborsLoaded(true);
        Set<Precinct> precincts = state.getPrecincts();
        for (Precinct precinct : precincts){
            Set<Precinct> neighbors = new HashSet<>();
            for (Precinct p : state.getPrecincts()){
                for (String neighborCode : precinct.getNeighborCodes()){
                    if (neighborCode.equals(p.getCode())){
                        neighbors.add(p);
                    }
                }
            }
            precinct.setNeighbors(neighbors);
        }
        System.out.println("Neighbors Loaded");
    }

    /* Phase 0 */
    public List<VoteBlocResult> runPhase0(float blocThreshold, float voteThreshold){
        return this.state.findVoteBlocs(blocThreshold, voteThreshold);
    }

    /* Phase 1 */
    public List<Phase1Result> runPhase1(List<Demographic> demographics, float demographicMinimum, float demographicMaximum, int targetDistrictNum){
        if(!incrementalRunning){
            state.initClusters();
        }
        float targetPopulation = (float) state.getPopulation() / targetDistrictNum;

        // Create initial clusters
        while(state.getClusters().size() > targetDistrictNum) {
            state.resetPairs();
            state.setMMPairs(demographicMinimum, demographicMaximum, demographics);
            state.setPairs(targetPopulation);
            state.mergePairs(targetDistrictNum);
        }

        // Creating Result objects
        List<Phase1Result> results = new ArrayList<>();
        for(Cluster c : state.getClusters()){
                List<String> precinctCodes = new ArrayList<>();
                for(Precinct p : c.getPrecincts())
                        precinctCodes.add(p.getCode());
                results.add(new Phase1Result(precinctCodes));
        }
        incrementalRunning = false;
        System.out.println(state.getClusters().size() + " clusters created");
        return results;
    }

    public List<Phase1Result> runPhase1Incremental(List<Demographic> demographics, float demographicMinimum, float demographicMaximum, int targetDistrictNum) {
        if(!incrementalRunning){
            state.initClusters();
        }
        float targetPopulation = (float) state.getPopulation() / targetDistrictNum;
        if(state.getClusters().size() > targetDistrictNum){
            state.resetPairs();
            state.setMMPairs(demographicMinimum, demographicMinimum, demographics);
            state.setPairs(targetPopulation);
            state.mergePairs(targetDistrictNum);
            System.out.println("CURRENT SIZE - " + state.getClusters().size());
        }
        if(state.getClusters().size() <= targetDistrictNum){
            incrementalRunning = false;
        }
        // Creating Result objects
        return createDistrictResults();
    }
    
    /* Phase 2 */
    public Phase2Result runPhase2(List<Measure> measures){
        DefaultMeasure measureFunction = new DefaultMeasure(measures);
        state.setScoreFunction(measureFunction);
        double[] scores = state.anneal();
        List<Phase1Result> districtResults = createDistrictResults();
        return new Phase2Result(scores[0], scores[1], districtResults);
    }

    public List<Phase1Result> createDistrictResults(){
        List<Phase1Result> results = new ArrayList<>();
        for(Cluster c : state.getClusters()){
            List<String> precinctCodes = new ArrayList<>();
            for(Precinct p : c.getPrecincts())
                precinctCodes.add(p.getCode());
            results.add(new Phase1Result(precinctCodes));
        }
        return results;
    }
}
