package com.grimoire.server.repository;

import com.grimoire.common.model.Session;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class SessionRepository extends JsonRepository<Session> {
    
    @Override
    protected String getSubDirectory() {
        return "sessions";
    }
    
    @Override
    protected Class<Session> getEntityClass() {
        return Session.class;
    }
    
    public void save(Session session) throws IOException {
        super.save(session, session.getId());
    }
    
    public Optional<Session> findById(String id) {
        return super.findById(id);
    }
    
    public List<Session> findByCampaignId(UUID campaignId) {
        return findAll().stream()
                .filter(session -> campaignId.equals(session.getCampaignId()))
                .collect(Collectors.toList());
    }
}
