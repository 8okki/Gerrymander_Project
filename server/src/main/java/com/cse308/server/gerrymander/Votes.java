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
    String precinctCode;
    
    public Votes(Map<PoliticalParty, Integer> votes, String precinctName){
        this.votes = votes;
        this.precinctCode = precinctCode;
    }
    
    public int getTotalVotes(){
        int output = 0;
        for (Integer voteNum : this.votes.values()){
            output += voteNum;
        }
        return output;
    }
    
    public PoliticalParty getWinningParty(){
        int curLargest = Integer.MAX_VALUE;
        PoliticalParty curParty = null;
        for(Map.Entry<PoliticalParty, Integer> entry : this.votes.entrySet()){
            if (entry.getValue() > curLargest){
                curLargest = entry.getValue();
                curParty = entry.getKey();
            }
        }
        return curParty;
    }
    
    public int getWinningVotes(){
        int curLargest = Integer.MAX_VALUE;
        for(Integer voteNum : this.votes.values()){
            if (voteNum > curLargest){
                curLargest = voteNum;
            }
        }
        return curLargest;
    }
    
    public VoteBlocResult getVoteBlocResult(Demographic demographic, float voteThreshold){
        int winningVotes = getWinningVotes();
        int totalVotes = getTotalVotes();
        float ratio = calculateRatio(winningVotes,totalVotes);
        boolean isVoteBloc = ratio > voteThreshold;
        return new VoteBlocResult(isVoteBloc, demographic, getWinningParty(), this.precinctName);
    }
    
    private static float calculateRatio(int winningVotes, int totalVotes){
        return (float)winningVotes/totalVotes;
    }
    
    private static boolean checkVoteThreshold(float ratio, float voteThreshold){
        return ratio > voteThreshold;
    }
}
