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

import java.util.*;
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

    @Transient
    private Set<Precinct> precincts;

    @Transient
    private Set<Cluster> clusters;

    @Transient
    private Map<Cluster,Cluster> pairs;

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
    
    public void initClusters(){
        this.clusters = new HashSet<>();
        Map<Precinct,Cluster> precinctsToClusters = new HashMap<>();
        for(Precinct precinct : this.precincts){
            Cluster cluster = new Cluster(precinct);
            this.clusters.add(cluster);
            precinctsToClusters.put(precinct, cluster);
        }
        for(Cluster cluster : clusters){
            Precinct precinct = (Precinct) cluster.getPrecincts().toArray()[0];
            for(Precinct neighbor : precinct.getNeighbors()){
                cluster.getAdjacentClusters().add(precinctsToClusters.get(neighbor));
            }
        }
    }

    public void setMMPairs(float minRange, float maxRange, List<Demographic> demographics) {
        pairs = new HashMap<>();
        for (Cluster cluster : clusters) {
            if (!pairs.containsKey(cluster)) {
                Cluster pair = cluster.findMMPair(minRange, maxRange, demographics);
                if (pair != null) {
                    pairs.put(cluster, pair);
                    pairs.put(pair, cluster);
                }
            }
        }
        for(Cluster pairedCluster : pairs.keySet()){
            clusters.remove(pairedCluster);
        }
    }

    public void setPairs(float targetPopulation) {
        for(Cluster cluster : clusters) {
            if (!pairs.containsKey(cluster)) {
                Cluster pair = cluster.findPair(targetPopulation);
                if (pair != null) {
                    pairs.put(cluster, pair);
                    pairs.put(pair, cluster);
                }
            }
        }
        for(Cluster pairedCluster : pairs.keySet()){
            clusters.remove(pairedCluster);
        }
    }

    public void mergePairs() {
        if(pairs.isEmpty()) {
            int currentMin = Integer.MAX_VALUE;
            Cluster[] minClusters = new Cluster[2];
            for (Cluster cluster : clusters) {
                for (Cluster neighbor : cluster.getAdjacentClusters()) {
                    int sum = cluster.getPopulation() + neighbor.getPopulation();
                    if (sum < currentMin) {
                        currentMin = sum;
                        minClusters[0] = cluster;
                        minClusters[1] = neighbor;
                    }
                }
            }
            pairs.put(minClusters[0], minClusters[1]);
        }
        for (Cluster cluster : pairs.keySet()) {
            if (!cluster.isMerged()) {
                cluster.merge(pairs.get(cluster));
                clusters.add(cluster);
            }
        }
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

    public Set<Precinct> getPrecincts() {
        return this.precincts;
    }

    public void setPrecincts(Set precincts) {
        this.precincts = precincts;
    }
    
    public DistrictInfo getDistrictInfo(int districtId, Demographic[] demographic){
        return null;
    }

    public Set<Cluster> getClusters() { 
        return clusters; 
    }

    @Override
    public String toString(){
        return "[Name: " + this.name.toString() +
                ", population: " + this.population + ",precincts: " + this.precincts + "]";
    }
}
