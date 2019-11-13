/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.gerrymander.result;

import com.cse308.server.gerrymander.enums.Demographic;
import com.cse308.server.gerrymander.enums.PoliticalParty;

/**
 *
 * @author Jakob
 */
public class VoteBlocResult implements Result {
    
    boolean isVoteBloc;
    Demographic demographic;
    PoliticalParty winningParty;
    String precinctName;
    
    public VoteBlocResult(boolean isVoteBloc, Demographic demographic, PoliticalParty winningParty, String precinctName){
        this.isVoteBloc = isVoteBloc;
        this.demographic = demographic;
        this.winningParty = winningParty;
        this.precinctName = precinctName;
    }
    
    public boolean getIsVoteBloc(){
        return this.isVoteBloc;
    }
    
    public Demographic getDemographic(){
        return this.demographic;
    }
    
    public PoliticalParty getWinningParty(){
        return this.winningParty;
    }
    public String getPrecinctName(){
        return this.precinctName;
    }
    
}
