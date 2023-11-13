package com.bitsbids.bitsbids.AnonymousUser;

import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "anonymous_users", uniqueConstraints = {
        @UniqueConstraint(name = "unique_user_id", columnNames = { "userId" }),
        @UniqueConstraint(name = "unique_user_name", columnNames = { "anonUsername" })
})
public class AnonymousUser {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID anonUserId;

    @Column(name = "userId", nullable = false)
    private UUID userId;

    @Column(name = "anonUsername", nullable = false)
    private String anonUsername;

}
