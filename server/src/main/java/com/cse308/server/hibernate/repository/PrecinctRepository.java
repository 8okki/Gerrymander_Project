/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.hibernate.repository;

import com.cse308.server.models.Precinct;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Jakob
 */
@Repository
public interface PrecinctRepository extends JpaRepository<Precinct, String> {
    Set<Precinct> findByState(String state);
}
