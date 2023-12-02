package com.bitsbids.bitsbids.Users;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable UUID userId) {
        return userService.getUserbyId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID userId) {
        boolean deleteSuccess = userService.deleteUser(userId);
        if (deleteSuccess) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{userId}/wallet")
    public ResponseEntity<BigDecimal> getWalletBalance(@PathVariable UUID userId) {
        return userService.getWalletBalance(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/wallet")
    public ResponseEntity<?> updateWalletBalance(@PathVariable UUID userId, @RequestBody WalletUpdateRequest request) {
        BigDecimal newBalance = userService.updateWalletBalance(userId, request.getAmount());
        if (newBalance != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("newBalance", newBalance);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body("Unable to update wallet balance.");
        }
    }

    @PutMapping("/{userId}/info")
    public ResponseEntity<?> updateUserContactInfo(
            @PathVariable UUID userId,
            @RequestBody Map<String, String> updateRequest) {
        try {
            String phoneNumber = updateRequest.get("phoneNo");
            String hostel = updateRequest.get("hostel");

            if (phoneNumber == null || phoneNumber.isEmpty() || hostel == null || hostel.isEmpty()) {
                return ResponseEntity.badRequest().body("Phone number and hostel are required.");
            }

            boolean updateSuccess = userService.updateUserContactInfo(userId, phoneNumber, hostel);

            if (updateSuccess) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UUID> getCurrentAuthenticatedUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            User user = (User) principal;
            String email = user.getEmail();

            return userService.getUserbyEmail(email)
                    .map(User::getUserId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // TODO - get bids, sales
}
