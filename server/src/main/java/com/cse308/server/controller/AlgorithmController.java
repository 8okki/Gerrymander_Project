/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.controller;

import com.cse308.server.gerrymander.Algorithm;
import com.cse308.server.gerrymander.State;
import com.cse308.server.gerrymander.enums.StateName;
import com.cse308.server.service.AlgorithmService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
//import com.google.gson.Gson;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
    
    @PostMapping(value = "/initState", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonObject initStateRequest(@RequestBody JsonObject stateJson) {
        JsonObject responseBody = new JsonObject();
        try {
            StateName stateName = StateName.valueOf(stateJson.get("stateName").getAsString());
            State state = algoService.initState(stateName);
            if(state != null){
                responseBody.addProperty("name", state.getName());
                responseBody.addProperty("population", state.getPopulation());
                return responseBody;
            }else{
                responseBody.addProperty("error", "Couldn't initialize state");
                return responseBody;
            }
        } catch (Exception e) {
            responseBody.addProperty("error", e.toString());
            return responseBody;
        }
    }
    
    @PostMapping(value = "/runPhase0", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonObject phase0Request(@RequestBody JsonObject input){
        JsonObject responseBody = new JsonObject();
        try {
            float blocThreshold = input.get("blocThreshold").getAsFloat();
            float voteThreshold = input.get("blocThreshold").getAsFloat();
            algoService.runPhase0(blocThreshold, voteThreshold);
            return responseBody;
        } catch (Exception e) {
            responseBody.addProperty("error", e.toString());
            return responseBody;
        }
    }
    
}
