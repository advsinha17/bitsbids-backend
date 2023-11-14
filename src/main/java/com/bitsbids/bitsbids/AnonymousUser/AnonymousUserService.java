package com.bitsbids.bitsbids.AnonymousUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class AnonymousUserService {

    @Autowired
    private AnonymousUserRepository anonymousUserRepository;

    @Autowired
    private AnonUsernameGenerator anonUsernameGenerator;

    public List<AnonymousUser> getAnonymousUsers() {
        return anonymousUserRepository.findAll();
    }

    public Optional<AnonymousUser> getAnonUserById(UUID id) {
        return anonymousUserRepository.findById(id);
    }

    public Optional<AnonymousUser> getAnonUserByUserId(UUID userId) {
        return anonymousUserRepository.findByUser_Id(userId);
    }

    public Optional<AnonymousUser> getAnonUserByUsername(String anonUsername) {
        return anonymousUserRepository.findByAnonUsername(anonUsername);
    }

    public AnonymousUser addAnonymousUser(AnonymousUser anonymousUser) {
        String anonUsername;
        Optional<AnonymousUser> existingUser;

        do {
            anonUsername = anonUsernameGenerator.generateUsername();
            existingUser = anonymousUserRepository.findByAnonUsername(anonUsername);
        } while (existingUser.isPresent());

        anonymousUser.setAnonUsername(anonUsername);
        return anonymousUserRepository.save(anonymousUser);
    }

    @Transactional
    public boolean deleteUser(UUID id) {
        boolean exists = anonymousUserRepository.existsById(id);
        if (!exists) {
            return false;
        }

        try {
            anonymousUserRepository.deleteById(id);
            return !anonymousUserRepository.existsById(id);
        } catch (Exception e) {
            return false;
        }
    }

}
