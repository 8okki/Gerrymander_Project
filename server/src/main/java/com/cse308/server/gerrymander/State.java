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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Mavericks
 */

@NamedQueries({
        @NamedQuery(name = "State_findByName",
                query = "from State where id = :ID")
})

/*@NamedNativeQueries({
        @NamedNativeQuery(name = "State_findByName", query = "select * from states where name = :NAME", resultClass = State.class)
})*/

@Entity
@Table(name="states")
public class State {
    @Id
    private String name;
    private int population;
    
    @OneToMany(mappedBy="state",fetch=FetchType.EAGER)
    private Set<Precinct> precincts;
    
    @Transient
    private Set<Cluster> clusters;
    @Transient
    private Map<Cluster,Cluster> mmPairs;

    public State() {}
    
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
    public void setName(String name) { 
        this.name = name;
    }
    public void setPopulation(int population) { 
        this.population = population;
    }
    
    public Set<Precinct> getPrecincts() { return this.precincts; }
    void setPrecincts(Set precincts) { this.precincts = precincts; }
    
    @Override
    public String toString(){
        return "[Name: " + this.name.toString() + 
                ", population: " + this.population + ",precincts: " + this.precincts + "]"; 
    }
}
