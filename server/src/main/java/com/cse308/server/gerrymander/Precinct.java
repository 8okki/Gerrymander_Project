/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.gerrymander;

import com.cse308.server.gerrymander.enums.Demographic;
import com.cse308.server.gerrymander.enums.StateName;
import com.cse308.server.gerrymander.result.VoteBlocResult;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Maverick
 */
@Entity
@Table(name="precincts")
public class Precinct {
    @Id
    private String code;
    private String name;
    private int population;
    
    @ManyToOne
    @JoinColumn(name="state", nullable=false)
    private State state;
    
    @OneToOne(mappedBy="precinct",fetch=FetchType.EAGER)
    private Votes electionVotes;
    
    @ElementCollection(fetch=FetchType.EAGER)
    @MapKeyColumn(name = "demographic")
    @MapKeyEnumerated(EnumType.STRING)
    private Map<Demographic, Integer> demographicPopulationDist;
    
    @Transient
    private Set<Precinct> neighbors;
    
    public Precinct(){}
    
    public Precinct(String code, String name, int population, Map<Demographic, Integer> demographicPopulationDist, Votes electionVotes){
        this.code = code;
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
    public String getCode(){
        return this.code;
    }
    
    public String getName(){
        return this.name;
    }
    
    public int getPopulation(){
        return this.population;
    }
    
    public State getState(){
        return this.state;
    }
    
    public Votes getElectionVotes(){
        return this.electionVotes;
    }
    
    public void setCode(String code) { 
        this.code = code;
    }
    
    public void setName(String name) { 
        this.name = name;
    }
    public void setPopulation(int population) { 
        this.population = population;
    }
    
    public void setDemographicPopDist(Map<Demographic, Integer> demographicPopDist){
        this.demographicPopulationDist = demographicPopDist;
    }
    
    public void setState(State state){
        this.state = state;
    }
    
    public void setElectionVotes(Votes electionVotes){
        this.electionVotes = electionVotes;
    }
    
    public Map<Demographic, Integer> getDemographicPopDist(){
        return this.demographicPopulationDist;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Precinct)) return false;
        Precinct p  = (Precinct) o;
        return Objects.equals(p.code, this.code);
    }
    
    @Override
    public String toString(){
        return "[code:" + this.code + ",dist:" + this.getDemographicPopDist() + ",election:" + this.getElectionVotes() + "]";
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.code);
    }
    
}
