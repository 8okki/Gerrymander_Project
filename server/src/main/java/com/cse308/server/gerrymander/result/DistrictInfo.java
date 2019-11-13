/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.gerrymander.result;

/**
 *
 * @author Jakob
 */
public class DistrictInfo implements Result {
    
    Map<DEMOGRAPHIC, int> demoPopDist;
    int districtPop;
    int statePop;
    
    public Map<DEMOGRAPHIC, int> getDemoPopDist(){
        return this.demoPopDist;
    }
    
    public int getDistrictPop(){
        return this.districtPop;
    }
    
    public int getStatePop(){
        return this.statePop;
    }
    
}
