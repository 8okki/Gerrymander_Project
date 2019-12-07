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
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Mavericks
 */
@Entity
@Table(name="votes")
public class Votes {
    @Id
    @Column(name="precinct_name")
    String precinctName;
    
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(
        name = "party_votes",
        joinColumns=@JoinColumn(name = "precinct_name", referencedColumnName = "precinct_name")
    )
    @Column(name="votes")
    @MapKeyColumn(name = "politicalparty")
    @MapKeyEnumerated(EnumType.STRING)
    Map<PoliticalParty, Integer> partyVotes;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "precinct")
    private Precinct precinct;
    
    public Votes(){} //hibernate seems to require empty constructors
    
    public Votes(Map<PoliticalParty, Integer> partyVotes, String precinctName){
        this.partyVotes = partyVotes;
        this.precinctName = precinctName;
    }
    
    public int getTotalVotes(){
        int output = 0;
        for (Integer voteNum : this.partyVotes.values()){
            output += voteNum;
        }
        return output;
    }
    
    public PoliticalParty getWinningParty(){
        int curLargest = -1;
        PoliticalParty curParty = null;
        for(Map.Entry<PoliticalParty, Integer> entry : this.partyVotes.entrySet()){
            if (entry.getValue() > curLargest){
                curLargest = entry.getValue();
                curParty = entry.getKey();
            }
        }
        return curParty;
    }
    
    public int getWinningVotes(){
        int curLargest = -1;
        for(Integer voteNum : this.partyVotes.values()){
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
        return new VoteBlocResult(isVoteBloc, demographic, this.getWinningParty(), this.precinctName);
    }
    
    private static float calculateRatio(int winningVotes, int totalVotes){
        return (float)winningVotes/totalVotes;
    }
    
    private static boolean checkVoteThreshold(float ratio, float voteThreshold){
        return ratio > voteThreshold;
    }
    
    public String toString(){
        return "" + this.getTotalVotes();
    }
    
    public String getPrecinctName(){
        return this.precinctName;
    }
    
    public Map<PoliticalParty, Integer> getVotes(){
        return this.partyVotes;
    }
    
    public void setVotes(Map<PoliticalParty, Integer> partyVotes){
        this.partyVotes = partyVotes;
    }
    
    public void setPrecinctName(String precinctName){
        this.precinctName = precinctName;
    }
    
}
