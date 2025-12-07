package com.grimoire.server.repository;

import com.grimoire.common.model.Campaign;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CampaignRepository extends JsonRepository<Campaign> {
    
    @Override
    protected String getSubDirectory() {
        return "campaigns";
    }
    
    @Override
    protected Class<Campaign> getEntityClass() {
        return Campaign.class;
    }
    
    public void save(Campaign campaign) throws IOException {
        super.save(campaign, campaign.getId().toString());
    }
    
    public Optional<Campaign> findById(UUID id) {
        return super.findById(id.toString());
    }
}