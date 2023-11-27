package com.bitsbids.bitsbids.AnonymousUser;

import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import com.bitsbids.bitsbids.Product.Product;
import com.bitsbids.bitsbids.Users.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
        @UniqueConstraint(name = "unique_user_name", columnNames = { "anon_username" })
})
public class AnonymousUser {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "anon_user_id", nullable = false, updatable = false)
    private UUID anonUserId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @Column(name = "anon_username", nullable = false)
    private String anonUsername;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    public enum UserRole {
        SELLER,
        BIDDER,
        CHATTER
    }

}
