/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.models;

import com.cse308.server.algorithm.Move;
import com.cse308.server.enums.Demographic;
import com.cse308.server.result.DistrictInfo;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.algorithm.MinimumBoundingCircle;

import static com.cse308.server.enums.PoliticalParty.DEMOCRATIC;
import static com.cse308.server.enums.PoliticalParty.REPUBLICAN;

import java.util.*;


/**
 *
 * @author Mavericks
 */
public class Cluster {
    public int id;

    private State state;
    private Set<Precinct> precincts;
    private Set<Precinct> borderPrecincts;
    private Set<Cluster> adjClusters;

    private int population;
    private Map<Demographic, Integer> demographicPopDist;

    private int repVote;
    private int demVote;

    private int internalEdges;
    private int externalEdges;

    private MultiPolygon multiPolygon;
    private Geometry boundingCircle;
    private Geometry convexHull;

    private boolean boundingCircleUpdated;
    private boolean multiPolygonUpdated;
    private boolean convexHullUpdated;

    private boolean isMerged;


    /* Getters & Setters */
    public State getState() { return state; }

    public Set<Precinct> getPrecincts(){
        return this.precincts;
    }

    public Set<Precinct> getBorderPrecincts() { return borderPrecincts; }

    public Set<Cluster> getAdjacentClusters(){
        return this.adjClusters;
    }

    public int getPopulation(){
        return this.population;
    }

    public Map<Demographic, Integer> getDemographicPopDist(){
        return this.demographicPopDist;
    }

    public Map<Demographic, Integer> getDemographicPopDist(Demographic[] demographics){
        HashMap<Demographic, Integer> output = new HashMap<Demographic, Integer>();
        for(int i = 0; i < demographics.length; i++){
            for(Map.Entry<Demographic, Integer> entry : this.demographicPopDist.entrySet()){
                if (demographics[i].equals(entry.getKey())){
                    output.put(entry.getKey(),entry.getValue());
                }
            }
        }
        return output;
    }

    public int getRepVote() { return repVote; }

    public int getDemVote() { return demVote; }

    public int getInternalEdges() { return internalEdges; }

    public int getExternalEdges() { return externalEdges; }

    public DistrictInfo getDistrictInfo(int statePopulation, Demographic[] demographics){
        return null;
    }

    public boolean isMerged() { return isMerged; }

    public void setAdjClusters(Set<Cluster> adjClusters) { this.adjClusters = adjClusters; }

    public void setIsMerged(boolean isMerged) { this.isMerged = isMerged; }

    /* Constructor */
    public Cluster(int id, State state, Precinct precinct) {
        this.id = id;
        this.state = state;
        precincts = new HashSet<>();
        adjClusters = new HashSet<>();
        borderPrecincts = new HashSet<>();
        demographicPopDist = new HashMap<>();
        for(Demographic demographic : Demographic.values())
            demographicPopDist.put(demographic, 0);

        addPrecinct(precinct);
    }


    /* Basic Functions */
    public void addPrecinct(Precinct precinct) {
        // Add target precinct & its data
        precincts.add(precinct);
        precinct.setCurrentCluster(this);
        population += precinct.getPopulation();
        repVote += precinct.getElectionVotes().getVotes().get(REPUBLICAN);
        demVote += precinct.getElectionVotes().getVotes().get(DEMOCRATIC);
        for(Demographic demographic : precinct.getDemographicPopDist().keySet()) {
            int demoPop = demographicPopDist.get(demographic) + precinct.getDemographicPopDist().get(demographic);
            demographicPopDist.put(demographic, demoPop);
        }

        // Update edge counts
        Set<Precinct> newInternalNeighbors = getInternalNeighbors(precinct);
        int newInternalEdges = newInternalNeighbors.size();
        internalEdges += newInternalEdges;
        externalEdges += (precinct.getNeighbors().size() - 2 * newInternalEdges);

        // Update border precincts
        newInternalNeighbors.removeIf(this::isBorderPrecinct);
        borderPrecincts.removeAll(newInternalNeighbors);
        borderPrecincts.add(precinct);

        multiPolygonUpdated = false;
        convexHullUpdated = false;
        boundingCircleUpdated = false;
    }

    public void removePrecinct(Precinct precinct) {
        // Remove target precinct & its data
        precincts.remove(precinct);
        population -= precinct.getPopulation();
        repVote -= precinct.getElectionVotes().getVotes().get(REPUBLICAN);
        demVote -= precinct.getElectionVotes().getVotes().get(DEMOCRATIC);
        for(Demographic demographic : precinct.getDemographicPopDist().keySet()) {
            int demoPop = demographicPopDist.get(demographic) - precinct.getDemographicPopDist().get(demographic);
            demographicPopDist.put(demographic, demoPop);
        }

        // Update edge counts
        Set<Precinct> lostInternalNeighbors = getInternalNeighbors(precinct);
        int lostInternalEdges = lostInternalNeighbors.size();
        internalEdges -= lostInternalEdges;
        externalEdges -= (precinct.getNeighbors().size() - 2 * lostInternalEdges);

        // Update border precincts
        borderPrecincts.addAll(lostInternalNeighbors);
        borderPrecincts.remove(precinct);

        multiPolygonUpdated = false;
        convexHullUpdated = false;
        boundingCircleUpdated = false;
    }


    /* Phase 1 */
    public Cluster findMMPair(float minRange, float maxRange, List<Demographic> demographics){
        for(Cluster cluster : adjClusters) {
            if(!cluster.isMerged() && isMMPair(cluster, demographics, minRange, maxRange)) {
                return cluster;
            }
        }
        return null;
    }

    public Cluster findPair(float targetPopulation) {
        for(Cluster cluster : adjClusters) {
            if(!cluster.isMerged() && isPair(cluster, targetPopulation)){
                return cluster;
            }
        }
        return null;
    }

    public void merge(Cluster cluster) {
        for (Precinct precinct : cluster.getPrecincts())
            addPrecinct(precinct);

        unlink(cluster);
    }

    public void unlink(Cluster cluster){
        cluster.getAdjacentClusters().remove(this);
        adjClusters.remove(cluster);
        this.state.getClusters().remove(cluster);

        for(Cluster neighbor : cluster.getAdjacentClusters()){
            neighbor.getAdjacentClusters().add(this);
            neighbor.getAdjacentClusters().remove(cluster);
            adjClusters.add(neighbor);
        }
        cluster.setAdjClusters(new HashSet<>());
    }

    public int getDemographicPopSum(List<Demographic> demographics){
        int sum = 0;
        for(Demographic demographic : demographics) {
            sum += this.demographicPopDist.get(demographic);
        }
        return sum;
    }

    private static float calculateRatio(int demographicPopSum, int populationSum){
        return (float)demographicPopSum / populationSum;
    }
    
    private boolean isMMPair(Cluster cluster, List<Demographic> demographics, float minRange, float maxRange){
        int pairDemographicPopSum = this.getDemographicPopSum(demographics) + cluster.getDemographicPopSum(demographics);
        int pairTotalPopulation = this.getPopulation() + cluster.getPopulation();
        float ratio = calculateRatio(pairDemographicPopSum, pairTotalPopulation);
        return ratio >= minRange && ratio <= maxRange;
    }

    private boolean isPair(Cluster cluster, float targetPopulation) {
        int populationSum = this.getPopulation() + cluster.getPopulation();
        return populationSum <= targetPopulation;
    }

    public String toString(){
        String str = id + " - ";
        for(Cluster neighbor : adjClusters){
            str += neighbor.id + ", ";
        }
        return str;
    }


    /* Phase 2 */
    public void anneal() {
        // Find all candidate neighbors
        Set<Precinct> candidates = getExternalNeighbors();

        // Find best move
        Move move = findBestMove(candidates);

        // Execute the move
        if (move != null)
            move.execute();
    }

    public Move findBestMove(Set<Precinct> precincts) {
        Move bestMove = null;
        double bestScore = Double.MIN_VALUE;

        Move currentMove;
        double score;
        for (Precinct precinct : precincts) {
            Cluster to = this;
            Cluster from = precinct.getCurrentCluster();

            currentMove = new Move(precinct, from, to);
            currentMove.execute();
            score = state.getClusterScoreFunction().calculateMeasure(this);
            if (score >= bestScore) {
                bestMove = currentMove;
                bestScore = score;
            }
            currentMove.undo();
        }

        return bestMove;
    }

    public Set<Precinct> getExternalNeighbors() {
        Set<Precinct> externals = new HashSet<>();
        for (Precinct precinct : borderPrecincts)
            externals.addAll(precinct.getNeighbors());
        externals.removeAll(precincts);
        return externals;
    }

    public Set<Precinct> getInternalNeighbors(Precinct precinct) {
        Set<Precinct> internalNeighbors = new HashSet<>();
        for(Precinct neighbor : precinct.getNeighbors())
            if(neighbor.getCurrentCluster() == this)
                internalNeighbors.add(neighbor);
        return internalNeighbors;
    }

    public boolean isBorderPrecinct(Precinct precinct) {
        // BorderPrecinct = precinct located at the boundary of this cluster
        for (Precinct neighbor : precinct.getNeighbors())
            if (neighbor.getCurrentCluster() != this)
                return true;
        return false;
    }


    /* Geometric Functions */
    public MultiPolygon computeMulti() {
        Polygon[] polygons = new Polygon[getPrecincts().size()];

        Iterator<Precinct> piter = getPrecincts().iterator();
        for(int i = 0; i < polygons.length; i++) {
            Geometry poly = piter.next().getGeometry();
            if (poly instanceof Polygon)
                polygons[i] = (Polygon) poly;
            else
                polygons[i] = (Polygon) poly.convexHull();
        }
        multiPolygon = new MultiPolygon(polygons, new GeometryFactory());
        multiPolygonUpdated = true;

        return multiPolygon;
    }

    public MultiPolygon getMulti() {
        if (this.multiPolygonUpdated && this.multiPolygon != null)
            return multiPolygon;
        return computeMulti();
    }

    public Geometry getConvexHull() {
        if (convexHullUpdated && convexHull !=null)
            return convexHull;
        convexHull = multiPolygon.convexHull();
        convexHullUpdated = true;
        return convexHull;
    }

    public Geometry getBoundingCircle() {
        if (boundingCircleUpdated && boundingCircle !=null)
            return boundingCircle;
        boundingCircle = new MinimumBoundingCircle(getMulti()).getCircle();
        boundingCircleUpdated = true;
        return boundingCircle;
    }
}
