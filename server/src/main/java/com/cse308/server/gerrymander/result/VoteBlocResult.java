/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.gerrymander.result;

/**
 *
 * @author Jakob
 */
public class VoteBlocResult implements Result {
    
    boolean isVoteBloc;
    DEMOGRAPHIC demographic;
    POLITICALPARTY winningParty;
    String precinctName;
    
    public boolean getIsVoteBloc(){
        return this.isVoteBloc;
    }
    
    public DEMOGRAPHIC getDemographic(){
        return this.demographic;
    }
    
    public POLITICALPARTY getWinningParty(){
        return this.winningParty;
    }
    public String getPrecinctName(){
        return this.precinctName;
    }
    
}
