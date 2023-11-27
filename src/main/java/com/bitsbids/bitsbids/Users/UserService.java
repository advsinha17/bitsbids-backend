package com.bitsbids.bitsbids.Users;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserbyId(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserbyEmail(String email) {
        return userRepository.findByEmail(email);
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
            user.setBidAmount(BigDecimal.ZERO);
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
    public BigDecimal updateWalletBalance(UUID userId, BigDecimal amount) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            BigDecimal newBalance = user.getWalletBalance().add(amount);
            user.setWalletBalance(newBalance);
            userRepository.save(user);
            return newBalance;
        }
        return null;
    }

    @Transactional
    public BigDecimal updateBidAmount(UUID userId, BigDecimal amount) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            BigDecimal newBidAmount = user.getBidAmount().add(amount);
            if (newBidAmount.compareTo(user.getWalletBalance()) > 0) {
                throw new InsufficientBalanceException("New bid amount exceeds wallet balance.");
            }
            user.setBidAmount(newBidAmount);
            userRepository.save(user);
            return newBidAmount;
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

    @Transactional
    public BigDecimal decreaseBidAmount(UUID userId, BigDecimal amount) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            BigDecimal newBidAmount = user.getBidAmount().subtract(amount);
            user.setBidAmount(newBidAmount);
            userRepository.save(user);
            return newBidAmount;
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

    public class InsufficientBalanceException extends RuntimeException {
        public InsufficientBalanceException(String message) {
            super(message);
        }
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
