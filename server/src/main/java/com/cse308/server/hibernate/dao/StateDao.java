/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.hibernate.dao;

import com.cse308.server.gerrymander.State;
import com.cse308.server.gerrymander.enums.StateName;
import com.cse308.server.hibernate.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author Jakob
 */
public class StateDao {
    public List<State> getStateById(StateName stateName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createNamedQuery("State_findByName",
                    State.class).setParameter("ID", stateName.toString());
            return query.getResultList();
        }
    }
}
