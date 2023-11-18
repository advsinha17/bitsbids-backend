package com.bitsbids.bitsbids.AnonymousUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bitsbids.bitsbids.Users.User;
import com.bitsbids.bitsbids.Users.UserService;

import jakarta.transaction.Transactional;

@Service
public class AnonymousUserService {

    @Autowired
    private AnonymousUserRepository anonymousUserRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AnonUsernameGenerator anonUsernameGenerator;

    public List<AnonymousUser> getAnonymousUsers() {
        return anonymousUserRepository.findAll();
    }

    public Optional<AnonymousUser> getAnonUserById(UUID id) {
        return anonymousUserRepository.findById(id);
    }

    public Optional<AnonymousUser> getAnonUserByUserId(UUID userId) {
        return anonymousUserRepository.findByUser_UserId(userId);
    }

    public Optional<AnonymousUser> getAnonUserByUsername(String anonUsername) {
        return anonymousUserRepository.findByAnonUsername(anonUsername);
    }

    private class AnonUserCreationException extends RuntimeException {
        public AnonUserCreationException(String message) {
            super(message);
        }
    }

    public AnonymousUser addAnonymousUser(UUID userId) {

        Optional<User> userOptional = userService.getUserbyId(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String anonUsername = anonUsernameGenerator.generateUsername();
            while (anonymousUserRepository.existsByAnonUsername(anonUsername)) {
                anonUsername = anonUsernameGenerator.generateUsername();
            }
            AnonymousUser newAnonymousUser = new AnonymousUser();
            newAnonymousUser.setUser(user);
            newAnonymousUser.setAnonUsername(anonUsername);
            return anonymousUserRepository.save(newAnonymousUser);

        }
        throw new AnonUserCreationException("User not found with ID: " + userId);
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
