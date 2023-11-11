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

    @Transactional
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

}
