/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.result;

import com.cse308.server.enums.Demographic;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Mavericks
 */
public class DistrictInfo implements Result {
   
    int id;
    Map<Demographic, Integer> demoPopDist;
    List<String> precincts;
    int districtPop;
    
    public DistrictInfo(int id, List<String> precincts, Map<Demographic, Integer> demoPopDist, int districtPop){
        this.id = id;
        this.precincts = precincts;
        this.demoPopDist = demoPopDist;
        this.districtPop = districtPop;
    }
    
    public Map<Demographic, Integer> getDemoPopDist(){
        return this.demoPopDist;
    }
    
    public int getDistrictPop(){
        return this.districtPop;
    }    
}
