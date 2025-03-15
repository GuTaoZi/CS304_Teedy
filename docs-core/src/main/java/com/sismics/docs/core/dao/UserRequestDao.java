package com.sismics.docs.core.dao;

import com.sismics.docs.core.model.jpa.UserRequest;
import com.sismics.util.context.ThreadLocalContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UserRequestDao {
    
    public String create(UserRequest userRequest) {
        userRequest.setId(UUID.randomUUID().toString());
        userRequest.setCreateDate(new Date());
        
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        em.persist(userRequest);
        
        return userRequest.getId();
    }
    
    public UserRequest getById(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        return em.find(UserRequest.class, id);
    }
    
    @SuppressWarnings("unchecked")
    public List<UserRequest> getPendingRequests() {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select ur from UserRequest ur where ur.status = :status");
        q.setParameter("status", "PENDING");
        return q.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<UserRequest> getRequestHistory() {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select ur from UserRequest ur order by ur.createDate desc");
        return q.getResultList();
    }
    
    public void updateStatus(UserRequest userRequest, String status) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        userRequest.setStatus(status);
        userRequest.setModificationDate(new Date());
        em.merge(userRequest);
    }
}