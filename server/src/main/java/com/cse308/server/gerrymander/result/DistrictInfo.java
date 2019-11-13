/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.gerrymander.result;

import com.cse308.server.gerrymander.enums.Demographic;
import java.util.Map;

/**
 *
 * @author Jakob
 */
public class DistrictInfo implements Result {
    
    Map<Demographic, Integer> demoPopDist;
    int districtPop;
    int statePop;
    
    public Map<Demographic, Integer> getDemoPopDist(){
        return this.demoPopDist;
    }
    
    public int getDistrictPop(){
        return this.districtPop;
    }
    
    public int getStatePop(){
        return this.statePop;
    }
    
}