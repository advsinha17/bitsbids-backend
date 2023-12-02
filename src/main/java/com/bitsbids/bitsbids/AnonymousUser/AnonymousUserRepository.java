package com.bitsbids.bitsbids.AnonymousUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnonymousUserRepository extends JpaRepository<AnonymousUser, UUID> {

    Optional<AnonymousUser> findByAnonUsername(String anonUsername);

    boolean existsByAnonUsername(String anonUsername);

    Optional<AnonymousUser> findByUser_UserIdAndProduct_ProductIdAndRole(UUID userId, UUID productId,
            AnonymousUser.UserRole role);

    List<AnonymousUser> findByUser_UserId(UUID userId);

}