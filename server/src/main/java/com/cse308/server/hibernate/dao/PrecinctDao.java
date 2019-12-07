/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.hibernate.dao;

import com.cse308.server.gerrymander.Precinct;
import com.cse308.server.hibernate.repository.PrecinctRepository;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mavericks
 */
@Service
public class PrecinctDao {
    @Autowired
    private PrecinctRepository precinctRepo;
    
    public Set<Precinct> getPrecinctsByState(String stateName) {
        Set<Precinct> precincts = precinctRepo.findByState(stateName);
        if (precincts == null){
            return null;
        }
        return precincts;
    }
}
