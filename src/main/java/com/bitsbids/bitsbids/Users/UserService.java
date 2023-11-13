package com.bitsbids.bitsbids.Users;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserbyId(UUID id) {
        return userRepository.findById(id);
    }

    public User addOrUpdateUser(User user) {
        Optional<User> existingUserOptional = userRepository.findByEmail(user.getEmail());

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            updateExistingUserDetails(existingUser, user);
            userRepository.save(existingUser);
            return existingUser;
        } else {
            user.setWalletBalance(BigDecimal.ZERO);
            userRepository.save(user);
            return user;
        }
    }

    private void updateExistingUserDetails(User existingUser, User newUserDetails) {
        existingUser.setFirstName(newUserDetails.getFirstName());
        existingUser.setLastName(newUserDetails.getLastName());
    }

    public Optional<BigDecimal> getWalletBalance(UUID userId) {
        return userRepository.findById(userId).map(User::getWalletBalance);
    }

    @Transactional
    public boolean updateWalletBalance(UUID userId, BigDecimal newBalance) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setWalletBalance(newBalance);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean deleteUser(UUID id) {
        boolean exists = userRepository.existsById(id);
        if (!exists) {
            return false;
        }

        try {
            userRepository.deleteById(id);
            return !userRepository.existsById(id);
        } catch (Exception e) {
            return false;
        }
    }

}
