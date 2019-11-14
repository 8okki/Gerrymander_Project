/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.gerrymander;

import com.cse308.server.gerrymander.enums.Demographic;
import com.cse308.server.gerrymander.enums.StateName;
import com.cse308.server.gerrymander.result.VoteBlocResult;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 *
 * @author Maverick
 */
@Entity
public class Precinct {
    @Id
    private String code;
    private String name;
    private StateName state;
    private int population;
    private String[] demographics;
    private Integer[] demographicPopulations;
    private Votes electionVotes;
    @Transient
    private Map<Demographic, Integer> demographicPopulationDist;
    @Transient
    private Set<Precinct> neighbors;
    
    public Precinct(String name, int population, Map<Demographic, Integer> demographicPopulationDist, Votes electionVotes){
        this.name = name;
        this.population = population;
        this.demographicPopulationDist = demographicPopulationDist;
        this.electionVotes = electionVotes;
    }
    
    public void setNeighbors(Set<Precinct> neighbors){
        this.neighbors = neighbors;
    }
    
    public VoteBlocResult findVoteBloc(float blocThreshold, float voteThreshold){
        Demographic maxDemographic = findDemographicBloc(blocThreshold);
        if(maxDemographic != null){
            return electionVotes.getVoteBlocResult(maxDemographic, voteThreshold);
        }else{
            return null;
        }
    }
    
    public Demographic findDemographicBloc(float threshold){
        Demographic maxDemographic = findLargestDemographic();
        int maxDemographicPop = getDemographicPop(maxDemographic);
        float ratio = calculateRatio(maxDemographicPop, population);
        if(ratio > threshold){
            return maxDemographic;
        }else{
            return null;
        }
    }
    
    public int getDemographicPop(Demographic maxDemographic){
        return demographicPopulationDist.get(maxDemographic);
    }
    
    public Map<Demographic, Integer> getDemographicDist(Demographic[] demographics){
        Map<Demographic, Integer> output = new HashMap<Demographic, Integer>();
        for(Demographic demographic : demographics){
            output.put(demographic,getDemographicPop(demographic));
        }
        return output;
    }
    
    private Demographic findLargestDemographic(){
        int maxPopulation = -1;
        Demographic maxDemographic = null;
        for(Map.Entry<Demographic, Integer> entry : this.demographicPopulationDist.entrySet()){
            if (entry.getValue() > maxPopulation){
                maxDemographic = entry.getKey();
                maxPopulation = entry.getValue();
            }
        }
        return maxDemographic;
    }
    
    private static float calculateRatio(int largestDemographicPop, int totalPop){
        return (float)largestDemographicPop/totalPop;
    }
}
