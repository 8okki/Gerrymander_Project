/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.result;

import com.cse308.server.enums.Demographic;
import com.cse308.server.enums.PoliticalParty;
import java.util.List;

/**
 *
 * @author Mavericks
 */
public class Phase1Result implements Result {
    List<String> precincts;
    int before;
    int after;
    
    public Phase1Result(List<String> precincts){
        this.precincts = precincts;
    }
}
