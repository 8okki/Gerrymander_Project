/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.controller;

import com.cse308.server.gerrymander.State;
import com.cse308.server.gerrymander.enums.StateName;
import com.google.gson.JsonObject;
//import com.google.gson.Gson;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;    
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Jakob
 */


@RestController
public class InitController {
    @PostMapping("/state")
    public String greetingPost(@RequestBody JsonObject state) {
        System.out.println(state.toString());
        return "ok";
    }
}
