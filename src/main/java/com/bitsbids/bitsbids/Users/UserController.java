package com.bitsbids.bitsbids.Users;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
        boolean updateSuccess = userService.updateWalletBalance(userId, request.getAmount());
        if (updateSuccess) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Unable to update wallet balance.");
        }
    }

    // TODO - get bids, sales
}
