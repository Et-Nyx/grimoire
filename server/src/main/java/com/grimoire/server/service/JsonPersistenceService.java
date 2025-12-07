package com.grimoire.server.service;

import com.grimoire.common.model.SessionNote;
import com.grimoire.common.model.CharacterSheet;
import com.grimoire.common.model.Campaign;
import com.grimoire.common.model.Session;
import com.grimoire.common.model.User;
import com.grimoire.server.repository.SessionNoteRepository;
import com.grimoire.server.repository.CampaignRepository;
import com.grimoire.server.repository.CharacterSheetRepository;
import com.grimoire.server.repository.SessionRepository;
import com.grimoire.server.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JsonPersistenceService {

    private final UserRepository userRepository;
    private final CampaignRepository campaignRepository;
    private final CharacterSheetRepository characterSheetRepository;
    private final SessionNoteRepository sessionNoteRepository;
    private final SessionRepository sessionRepository;

    public JsonPersistenceService(UserRepository userRepository,
                                CampaignRepository campaignRepository,
                                CharacterSheetRepository characterSheetRepository,
                                SessionNoteRepository sessionNoteRepository,
                                SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.campaignRepository = campaignRepository;
        this.characterSheetRepository = characterSheetRepository;
        this.sessionNoteRepository = sessionNoteRepository;
        this.sessionRepository = sessionRepository;
    }

    // Character Sheet methods
    public void saveCharacterSheet(CharacterSheet sheet) throws IOException {
        characterSheetRepository.save(sheet);
    }

    public Optional<CharacterSheet> loadCharacterSheet(String id) {
        return characterSheetRepository.findById(id);
    }

    public List<CharacterSheet> loadAllSheets() {
        return characterSheetRepository.findAll();
    }

    // Session Note methods
    public void saveNote(SessionNote note) throws IOException {
        sessionNoteRepository.save(note);
    }

    public Optional<SessionNote> loadNote(String id) {
        return sessionNoteRepository.findById(id);
    }

    public void deleteNote(String id) throws IOException {
        sessionNoteRepository.deleteById(id);
    }

    public List<SessionNote> loadAllNotes() {
        return sessionNoteRepository.findAll();
    }
    
    public List<SessionNote> loadNotesBySession(String sessionId) {
        return sessionNoteRepository.findBySessionId(sessionId);
    }

    // User methods
    public void saveUser(User user) throws IOException {
        userRepository.save(user);
    }

    public Optional<User> loadUser(String id) {
        return userRepository.findById(UUID.fromString(id));
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void deleteUser(String id) throws IOException {
        userRepository.deleteById(id);
    }

    // Campaign methods
    public void saveCampaign(Campaign campaign) throws IOException {
        campaignRepository.save(campaign);
    }

    public Optional<Campaign> loadCampaign(String id) {
        return campaignRepository.findById(UUID.fromString(id));
    }

    public List<Campaign> loadAllCampaigns() {
        return campaignRepository.findAll();
    }

    public void deleteCampaign(String id) throws IOException {
        campaignRepository.deleteById(id);
    }

    // Session methods
    public void saveSession(Session session) throws IOException {
        sessionRepository.save(session);
    }

    public Optional<Session> loadSession(String id) {
        return sessionRepository.findById(id);
    }

    public void deleteSession(String id) throws IOException {
        sessionRepository.deleteById(id);
    }

    public List<Session> loadAllSessions() {
        return sessionRepository.findAll();
    }

    public List<Session> loadSessionsByCampaign(UUID campaignId) {
        return sessionRepository.findByCampaignId(campaignId);
    }
}
