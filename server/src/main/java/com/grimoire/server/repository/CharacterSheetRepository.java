package com.grimoire.server.repository;

import com.grimoire.common.model.CharacterSheet;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Optional;

@Repository
public class CharacterSheetRepository extends JsonRepository<CharacterSheet> {
    
    @Override
    protected String getSubDirectory() {
        return "sheets";
    }
    
    @Override
    protected Class<CharacterSheet> getEntityClass() {
        return CharacterSheet.class;
    }
    
    public void save(CharacterSheet sheet) throws IOException {
        super.save(sheet, sheet.getId());
    }
    
    public Optional<CharacterSheet> findById(String id) {
        return super.findById(id);
    }
}