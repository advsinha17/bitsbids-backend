package com.bitsbids.bitsbids.AnonymousUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    // private ProductService productService;

    // @Autowired
    // public void setProductService(ProductService productService) {
    // this.productService = productService;
    // }

    @Autowired
    private AnonUsernameGenerator anonUsernameGenerator;

    public List<AnonymousUser> getAnonymousUsers() {
        return anonymousUserRepository.findAll();
    }

    public Optional<AnonymousUser> getAnonUserById(UUID id) {
        return anonymousUserRepository.findById(id);
    }

    public List<AnonymousUser> getAnonUserByUserId(UUID userId) {
        return anonymousUserRepository.findByUser_UserId(userId);
    }

    public Optional<AnonymousUser> getAnonUserByUsername(String anonUsername) {
        return anonymousUserRepository.findByAnonUsername(anonUsername);
    }

    public Optional<AnonymousUser> findAnonUserForUserAndProduct(UUID userId, UUID productId,
            AnonymousUser.UserRole role) {
        return anonymousUserRepository.findByUser_UserIdAndProduct_ProductIdAndRole(userId, productId, role);
    }

    private class AnonUserCreationException extends RuntimeException {
        public AnonUserCreationException(String message) {
            super(message);
        }
    }

    public List<UUID> getAnonIdsByRealUserId(UUID realUserId) {
        List<AnonymousUser> anonymousUsers = anonymousUserRepository.findByUser_UserId(realUserId);

        return anonymousUsers.stream()
                .map(AnonymousUser::getAnonUserId)
                .collect(Collectors.toList());
    }

    public AnonymousUser addAnonymousUser(UUID userId, AnonymousUser.UserRole role) {
        Optional<User> userOptional = userService.getUserbyId(userId);
        if (!userOptional.isPresent()) {
            throw new AnonUserCreationException("User not found with ID: " + userId);
        }
        User user = userOptional.get();

        String anonUsername = anonUsernameGenerator.generateUsername();
        while (anonymousUserRepository.existsByAnonUsername(anonUsername)) {
            anonUsername = anonUsernameGenerator.generateUsername();
        }

        AnonymousUser newAnonymousUser = new AnonymousUser();
        newAnonymousUser.setUser(user);
        newAnonymousUser.setAnonUsername(anonUsername);
        newAnonymousUser.setRole(role);

        return newAnonymousUser;

        // return anonymousUserRepository.save(newAnonymousUser);
    }

    public AnonymousUser saveAnonymousUser(AnonymousUser anonymousUser) {
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
