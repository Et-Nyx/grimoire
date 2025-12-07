package com.grimoire.server.repository;

import com.grimoire.common.model.SessionNote;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SessionNoteRepository extends JsonRepository<SessionNote> {
    
    @Override
    protected String getSubDirectory() {
        return "notes";
    }
    
    @Override
    protected Class<SessionNote> getEntityClass() {
        return SessionNote.class;
    }
    
    public void save(SessionNote note) throws IOException {
        super.save(note, note.getId());
    }
    
    public Optional<SessionNote> findById(String id) {
        return super.findById(id);
    }
    
    public List<SessionNote> findBySessionId(String sessionId) {
        return findAll().stream()
                .filter(note -> sessionId.equals(note.getSessionId()))
                .sorted(Comparator.comparing(SessionNote::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
}