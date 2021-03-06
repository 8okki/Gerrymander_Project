/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.controller;

import com.cse308.server.measure.Measure;
import com.cse308.server.models.State;
import com.cse308.server.enums.Demographic;
import com.cse308.server.enums.StateName;
import com.cse308.server.result.DistrictInfo;
import com.cse308.server.result.Phase1Result;
import com.cse308.server.result.Phase2Result;
import com.cse308.server.result.VoteBlocResult;
import com.cse308.server.service.AlgorithmService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Jakob
 */

@RestController
public class AlgorithmController {
    @Autowired
    AlgorithmService algoService;
    
    @PostMapping(value = "/initState",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> initStateRequest(@RequestBody JsonObject stateJson) {
        JsonObject responseBody = new JsonObject();
        try {
            StateName stateName = StateName.valueOf(stateJson.get("stateName").getAsString());
            State state = algoService.initState(stateName);
            if(state != null){
                responseBody.addProperty("name", state.getName());
                responseBody.addProperty("population", state.getPopulation());
                return new ResponseEntity<>(responseBody,HttpStatus.OK);
            }else{
                responseBody.addProperty("error", "Invalid request body");
                return new ResponseEntity<>(responseBody,HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            System.out.println(e);
            responseBody.addProperty("error", "Invalid request body");
            return new ResponseEntity<>(responseBody,HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping(value = "/initGeometry",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> initGeometryRequest(@RequestBody JsonObject stateJson) {
        JsonObject responseBody = new JsonObject();
        try {
            algoService.initGeometry();
            return new ResponseEntity<>(responseBody,HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            responseBody.addProperty("error", "Invalid request body");
            return new ResponseEntity<>(responseBody,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/initNeighbors",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> initNeighborsRequest(@RequestBody JsonObject stateJson) {
        JsonObject responseBody = new JsonObject();
        try {
            algoService.initNeighbors();
            return new ResponseEntity<>(responseBody,HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            responseBody.addProperty("error", "Invalid request body");
            return new ResponseEntity<>(responseBody,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/runPhase0", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> phase0Request(@RequestBody JsonObject input){
        JsonObject responseBody = new JsonObject();
        try {
            float blocThreshold = input.get("blocThreshold").getAsFloat();
            float voteThreshold = input.get("voteThreshold").getAsFloat();
            ArrayList<VoteBlocResult> voteBlocResults = (ArrayList<VoteBlocResult>)algoService.runPhase0(blocThreshold, voteThreshold);
            JsonArray jsonResults = (JsonArray) new Gson().toJsonTree(voteBlocResults);
            responseBody.add("results",jsonResults);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.toString());
            responseBody.addProperty("error", "Invalid request body");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }
    }
	
    @PostMapping(value = "/runPhase1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> phase1Request(@RequestBody JsonObject input){
        JsonObject responseBody = new JsonObject();
        try {
            JsonArray demographicsAsStrings = input.get("demographics").getAsJsonArray();
            List<Demographic> demographics = new ArrayList<>();
            for(JsonElement demographic : demographicsAsStrings){
                demographics.add(Demographic.valueOf(demographic.getAsString()));
            }
            float demographicMinimum = input.get("demographicMinimum").getAsFloat();
            float demographicMaximum = input.get("demographicMaximum").getAsFloat();
            int targetDistrictNum = input.get("targetDistrictNum").getAsInt();
            List<DistrictInfo> results = algoService.runPhase1(demographicMinimum, demographicMaximum, demographics, targetDistrictNum);
            JsonArray jsonResults = (JsonArray) new Gson().toJsonTree(results);
            responseBody.add("districts",jsonResults);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            responseBody.addProperty("error", "Invalid request body");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping(value = "/runPhase1Incremental", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> phase1RequestIncremental(@RequestBody JsonObject input){
        JsonObject responseBody = new JsonObject();
        try {
            JsonArray demographicsAsStrings = input.get("demographics").getAsJsonArray();
            List<Demographic> demographics = new ArrayList<>();
            for(JsonElement demographic : demographicsAsStrings){
                demographics.add(Demographic.valueOf(demographic.getAsString()));
            }
            float demographicMinimum = input.get("demographicMinimum").getAsFloat();
            float demographicMaximum = input.get("demographicMaximum").getAsFloat();
            int targetDistrictNum = input.get("targetDistrictNum").getAsInt();
            List<DistrictInfo> results = algoService.runPhase1Incremental(demographicMinimum, demographicMaximum, demographics, targetDistrictNum);
            JsonArray jsonResults = (JsonArray) new Gson().toJsonTree(results);
            JsonElement isFinished = (JsonElement) new Gson().toJsonTree(algoService.isPhase1Done());
            responseBody.add("districts",jsonResults);
            responseBody.add("finished",isFinished);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            responseBody.addProperty("error", "Invalid request body");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/runPhase2", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> phase2Request(@RequestBody JsonObject input){
        JsonObject responseBody = new JsonObject();
        try{
            JsonObject measureWeightsJson = input.get("measureWeights").getAsJsonObject();
            List<Measure> measures = new ArrayList<>();
            for(Measure measure : Measure.values()) {
                if (measureWeightsJson.has(measure.name())) {
                    measure.setWeight(measureWeightsJson.get(measure.name()).getAsInt());
                    measures.add(measure);
                }
            }
            Phase2Result result = algoService.runPhase2(measures);
            JsonElement jsonResult = new Gson().toJsonTree(result);
            responseBody.add("result", jsonResult);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            responseBody.addProperty("error", "Invalid request body");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/getClusters", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getClustersRequest(){
        JsonObject responseBody = new JsonObject();
        try{
            JsonElement jsonResult = new Gson().toJsonTree(algoService.getClusters());
            responseBody.add("result", jsonResult);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            responseBody.addProperty("error", "Invalid request body");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }
    }

}
