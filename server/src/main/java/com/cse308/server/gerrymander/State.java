/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.gerrymander;

import com.cse308.server.gerrymander.enums.Demographic;
import com.cse308.server.gerrymander.enums.StateName;
import com.cse308.server.gerrymander.result.DistrictInfo;
import com.cse308.server.gerrymander.result.VoteBlocResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Mavericks
 */

@NamedNativeQueries({
        @NamedNativeQuery(name = "State_findByName", query = "select * from states where name = :NAME", resultClass = State.class)
})

@Entity
@Table(name="States")
public class State {
    @Enumerated(EnumType.STRING)
    @Id
    private StateName name;
    private int population;
    @Transient
    private Set<Cluster> clusters;
    @Transient
    private Set<Precinct> precincts;
    @Transient
    private Map<Cluster,Cluster> mmPairs;
    
    public List<VoteBlocResult> findVoteBlocs(float blocThreshold, float voteThreshold){
        List<VoteBlocResult> voteBlocResults = new ArrayList<>();
        for(Precinct precinct : this.precincts){
            VoteBlocResult result = precinct.findVoteBloc(blocThreshold, voteThreshold);
            if(result != null){
                voteBlocResults.add(result);
            }
        }
        return voteBlocResults;
    }
    
    public DistrictInfo getDistrictInfo(int districtId, Demographic[] demographic){
        return null;
    }
    
    public Map<Cluster,Cluster> setMMPairs(float minRange, float maxRange, Demographic[] demographics){
        return null;
    }
    
    public String getName(){
        return this.name.toString();
    }
    
    public int getPopulation(){
        return this.population;
    }
    
    @Override
    public String toString(){
        return "[Name: " + this.name.toString() + 
                ", population: " + this.population + "]"; 
    }
}
