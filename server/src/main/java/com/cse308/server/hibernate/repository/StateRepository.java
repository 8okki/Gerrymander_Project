/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.hibernate.repository;

import com.cse308.server.models.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Jakob
 */
@Repository
public interface StateRepository extends JpaRepository<State, String> {
    
}
