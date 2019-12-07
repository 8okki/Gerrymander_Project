/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.hibernate.dao;

import com.cse308.server.gerrymander.Precinct;
import java.util.logging.Logger;

import com.cse308.server.gerrymander.State;
import com.cse308.server.gerrymander.enums.StateName;
import com.cse308.server.hibernate.repository.StateRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mavericks
 */
@Service
public class StateDao {
    @Autowired 
	private PrecinctDao precinctDao;
	
    @Autowired
    private StateRepository stateRepo;
    
    public State getStateById(String stateName) {
        State state = stateRepo.getOne(stateName);
        if (state == null){
                return null;
        }
        Set<Precinct> precincts = precinctDao.getPrecinctsByState(stateName);
        /*for(Precinct p : precincts){
            System.out.print("" + p + ":");
            System.out.println(p.getNeighbors());
        }*/
        state.setPrecincts(precincts);
        return state;
    }
}
