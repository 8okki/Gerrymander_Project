/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.gerrymander;

import com.cse308.server.gerrymander.enums.Demographic;
import com.cse308.server.gerrymander.enums.PoliticalParty;
import com.cse308.server.gerrymander.result.VoteBlocResult;
import java.util.Map;

/**
 *
 * @author Mavericks
 */
public class Votes {
    
    Map<PoliticalParty, Integer> votes;
    
    public int getTotalVotes(){
        return -1;
    }
    
    public PoliticalParty getWinningParty(){
        return null;
    }
    
    public int getWinningVotes(){
        return -1;
    }
    
    public VoteBlocResult getVoteBlockResult(Demographic demographic, float voteThreshold, String precinctName){
        return null;
    }
    
    private float calculateRatio(int winningVotes, int totalVotes){
        return (float)winningVotes/totalVotes;
    }
    
    private boolean checkVoteThreshold(float ratio, float voteThreshold){
        return false;
    }
}
