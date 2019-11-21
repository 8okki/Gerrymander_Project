/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.controller;

import com.cse308.server.gerrymander.Algorithm;
import com.cse308.server.gerrymander.State;
import com.cse308.server.gerrymander.enums.Demographic;
import com.cse308.server.gerrymander.enums.StateName;
import com.cse308.server.gerrymander.result.VoteBlocResult;
import com.cse308.server.service.AlgorithmService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
//import com.google.gson.Gson;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;    
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Jakob
 */

@RestController
public class AlgorithmController {
    AlgorithmService algoService = new AlgorithmService();
    
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
            List<Demographic> demographics = new ArrayList<Demographic>();
            for(JsonElement demographic : demographicsAsStrings){
                demographics.add(Demographic.valueOf(demographic.getAsString()));
            }
            float demographicMinimum = input.get("demographicMinimum").getAsFloat();
            float demographicMaximum = input.get("demographicMaximum").getAsFloat();
            int targetDistrictNum = input.get("targetDistrictNum").getAsInt();
            algoService.runPhase1(demographics, demographicMinimum, demographicMaximum, targetDistrictNum);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.toString());
            responseBody.addProperty("error", "Invalid request body");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }
    } 
}
