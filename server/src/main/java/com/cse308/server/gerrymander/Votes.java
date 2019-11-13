/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.gerrymander;

/**
 *
 * @author Mavericks
 */
public class Votes {
    
    Map<POLITICALPARTY, int> votes;
    
    public int getTotalVotes(){
        return null;
    }
    
    public POLITICALPARTY getWinningParty(){
        return null;
    }
    
    public int getWinningVotes(){
        return null;
    }
    
    public VoteBlocResult getVoteBlockResult(DEMOGRAPHIC demographic, float voteThreshold, String precinctName){
        return null;
    }
    
    private float calculateRatio(int winningVotes, int totalVotes){
        return (float)winningVotes/totalVotes;
    }
    
    private boolean checkVoteThreshold(float ratio, float voteThreshold){
        return null;
    }
}
