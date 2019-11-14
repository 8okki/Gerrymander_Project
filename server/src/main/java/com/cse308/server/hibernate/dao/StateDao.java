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

/**
 *
 * @author Jakob
 */
public class StateDao {
    public List<State> getStateById(StateName stateName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("Statename: " + stateName.toString());
            System.out.println(session.createNamedQuery("State_findByName",
                    State.class).setParameter("ID", stateName.toString()).getResultList());
            return session.createNamedQuery("State_findByName",
                    State.class).setParameter("ID", stateName.toString()).getResultList();
        }
    }
}
