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
import javax.persistence.*;
import org.locationtech.jts.geom.Geometry;


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

    @Transient
    private Cluster currentCluster;

    @Column(name="geojson")
    private String geojson;
    
    @Transient
    private Geometry geometry;
    
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


    /* Getters & Setters */
    public String getCode(){
        return code;
    }

    public String getName(){
        return name;
    }

    public int getPopulation(){
        return population;
    }

    public State getState(){
        return state;
    }

    public Votes getElectionVotes(){
        return this.electionVotes;
    }

    public Map<Demographic, Integer> getDemographicPopDist(){
        return this.demographics;
    }

    public int getDemographicPop(Demographic maxDemographic){ return demographics.get(maxDemographic); }

    public String getGeojson(){
        return this.geojson;
    }
        
    public Geometry getGeometry() {
        return geometry;
    }

    public Set<Precinct> getNeighbors() { return neighbors; }

    public Cluster getCurrentCluster() { return currentCluster; }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setState(State state){
        this.state = state;
    }

    public void setGeojson(String geojson){
        this.geojson = geojson;
    }
    
    public void setGeometry(Geometry geometry) { this.geometry = geometry; }

    public void setCurrentCluster(Cluster cluster) { this.currentCluster = cluster; }

    /* Constructors */
    public Precinct(){}

    public Precinct(String code, String name, int population, Map<Demographic, Integer> demographics, Votes electionVotes){
        this.code = code;
        this.name = name;
        this.population = population;
        this.demographics = demographics;
        this.electionVotes = electionVotes;
    }


    /* Phase 0 */
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


    /* Phase 2 */
    public double getPopulationDensity() {
        if (geometry != null && geometry.getArea() != 0)
            return getPopulation() / geometry.getArea();
        return -1;
    }

}
