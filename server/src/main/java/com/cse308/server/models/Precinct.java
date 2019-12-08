/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.models;

import com.cse308.server.enums.Demographic;
import com.cse308.server.result.VoteBlocResult;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.OneToOne;
import javax.persistence.Table;


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
    
    @Column(name = "state")
    private String state;
    
    @Column(name = "geojson", columnDefinition="LONGTEXT")
    private String geojson;
    
    @OneToOne(mappedBy="precinct",fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private Votes electionVotes;
    
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(
        name = "demographics",
        joinColumns=@JoinColumn(name = "precinct_code", referencedColumnName = "code")
    )
    @Column(name = "population")
    @MapKeyColumn(name = "demographic")
    @MapKeyEnumerated(EnumType.STRING)
    private Map<Demographic, Integer> demographics;
    
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="neighbors",
        joinColumns=@JoinColumn(name="code"),
        inverseJoinColumns=@JoinColumn(name="neighbor_code")
    )
    private Set<Precinct> neighbors;
    
    @ManyToMany
    @JoinTable(name="neighbors",
        joinColumns=@JoinColumn(name="neighbor_code"),
        inverseJoinColumns=@JoinColumn(name="code")
    )
    private Set<Precinct> neighborsOf;
    
    public Precinct(){}
    
    public Precinct(String code, String name, int population, Map<Demographic, Integer> demographics, Votes electionVotes){
        this.code = code;
        this.name = name;
        this.population = population;
        this.demographics = demographics;
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
        return demographics.get(maxDemographic);
    }
    
    public Map<Demographic, Integer> getDemographicDist(Demographic[] demographics){
        Map<Demographic, Integer> output = new HashMap<Demographic, Integer>();
        for(Demographic demographic : demographics){
            output.put(demographic,getDemographicPop(demographic));
        }
        return output;
    }

    public Set<Precinct> getNeighbors() { 
        return this.neighbors; 
    }

    private Demographic findLargestDemographic(){
        int maxPopulation = -1;
        Demographic maxDemographic = null;
        for(Map.Entry<Demographic, Integer> entry : this.demographics.entrySet()){
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
    
    public String getState(){
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
    
    public void setDemographicPopDist(Map<Demographic, Integer> demographics){
        this.demographics = demographics;
    }
    
    public void setState(String state){
        this.state = state;
    }
    
    public void setElectionVotes(Votes electionVotes){
        this.electionVotes = electionVotes;
    }
    
    public Map<Demographic, Integer> getDemographicPopDist(){
        return this.demographics;
    }
    
    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Precinct)) return false;
        Precinct p  = (Precinct) o;
        return Objects.equals(p.name, this.name);
    }*/
    
    /*@Override
    public String toString(){
        return "[name:" + this.name + ",dist:" + this.getDemographicPopDist() + ",election:" + this.getElectionVotes() + "]";
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }
    */
}
