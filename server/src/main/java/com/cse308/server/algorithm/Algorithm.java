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
import java.util.HashMap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    Map<StateName,State> loadedStates;
    
    boolean incrementalRunning;
    
    public DistrictInfo getDistrictInfo(int districtId, Demographic[] demographics){
        return this.state.getDistrictInfo(districtId, demographics);
    }

    /* Initialize */
    public State initState(StateName stateName){
        if(loadedStates == null){
            loadedStates = new HashMap<>();
        }
        if(state == null || !loadedStates.containsKey(StateName.valueOf(state.getName()))){
            State result = stateDao.getStateById(stateName.name());
            if(result != null){
                state = result;
                loadedStates.put(stateName, state);
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
    public List<Phase1Result> runPhase1(float min, float max, List<Demographic> demoMM, int targetDistrictNum){
        if(!incrementalRunning){
            state.initClusters(min, max, demoMM);
        }
        float idealPopulation = (float) (state.getPopulation()) / targetDistrictNum;

        // Create initial clusters
        while(state.getClusters().size() > targetDistrictNum) {
            state.resetPairs();
            state.makeMMPairs(idealPopulation);
            state.makePairs(idealPopulation);
            state.mergePairs(targetDistrictNum);
            System.out.println("CURRENT SIZE: " + state.getClusters().size());
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

        int maxPop = Integer.MIN_VALUE, minPop = Integer.MAX_VALUE, mm = 0;
        for(Cluster cluster : state.getClusters()){
            if(cluster.getPopulation() > maxPop) {
                maxPop = cluster.getPopulation();
            }
            if(cluster.getPopulation() < minPop) {
                minPop = cluster.getPopulation();
            }
            if(cluster.isMM())
                mm++;
        }
        System.out.println(maxPop + " " + minPop + " " + (float) maxPop / minPop + " " + mm);

        return results;
    }

    public List<Phase1Result> runPhase1Incremental(List<Demographic> demographics, float demographicMinimum, float demographicMaximum, int targetDistrictNum){
        if(!incrementalRunning){
            state.initClusters();
        }
        float idealPopulation = (float) state.getPopulation() / targetDistrictNum;

        // Create initial clusters
        if(state.getClusters().size() > targetDistrictNum) {
            state.resetPairs();
            state.makeMMPairs(demographicMinimum, demographicMaximum, demographics, idealPopulation);
            state.makePairs(idealPopulation);
            state.mergePairs(targetDistrictNum);
            System.out.println("CURRENT CLUSTER COUNT: " + state.getClusters().size());
        }
        
        if(state.getClusters().size() <= targetDistrictNum){
            incrementalRunning = false;
        }else{
            incrementalRunning = true;
        }

        // Creating Result objects
        List<Phase1Result> results = new ArrayList<>();
        for(Cluster c : state.getClusters()){
            List<String> precinctCodes = new ArrayList<>();
            for(Precinct p : c.getPrecincts())
                    precinctCodes.add(p.getCode());
            results.add(new Phase1Result(precinctCodes));
        }
        System.out.println(state.getClusters().size() + " clusters created");

        int maxPop = Integer.MIN_VALUE, minPop = Integer.MAX_VALUE;
        for(Cluster cluster : state.getClusters()){
            if(cluster.getPopulation() > maxPop) {
                maxPop = cluster.getPopulation();
            }
            if(cluster.getPopulation() < minPop) {
                minPop = cluster.getPopulation();
            }
        }
        System.out.println(maxPop + " " + minPop + " " + (float) maxPop / minPop);

        return results;
    }
    
    public boolean isPhase1Done(){
        return !this.incrementalRunning;
    }
    
//    public List<Phase1Result> runPhase1Incremental(List<Demographic> demographics, float demographicMinimum, float demographicMaximum, int targetDistrictNum) {
//        if(!incrementalRunning){
//            state.initClusters();
//        }
//        float idealPopulation = (float) state.getPopulation() / targetDistrictNum;
//        if(state.getClusters().size() > targetDistrictNum){
//            state.resetPairs();
//            state.makeMMPairs(demographicMinimum, demographicMinimum, demographics, idealPopulation);
//            state.makePairs(idealPopulation);
//            state.mergePairs(targetDistrictNum, idealPopulation);
//            System.out.println("CURRENT SIZE - " + state.getClusters().size());
//        }
//        if(state.getClusters().size() <= targetDistrictNum){
//            incrementalRunning = false;
//        }
//        // Creating Result objects
//        return createDistrictResults();
//    }
    
    /* Phase 2 */
    public Phase2Result runPhase2(List<Measure> measures){
        return state.anneal(measures);
    }
}
