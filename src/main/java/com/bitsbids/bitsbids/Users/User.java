package com.bitsbids.bitsbids.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users",
uniqueConstraints = {
    @UniqueConstraint(name = "unique_email", columnNames = { "email" }),
    @UniqueConstraint(name = "unique_username", columnNames = { "username" }),
    @UniqueConstraint(name = "unique_phone", columnNames = { "phone_number" })
})
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(
        name = "user_id",
        nullable = false,
        updatable = false
    )
    private UUID userId;

    @Column(
        name = "username",
        nullable = false
    )
    private String username;

    @Column(
        name = "email",
        nullable = false
    )
    private String email;

    @Column(
        name = "password",
        nullable = false
    )
    private String password;

    @Column(
        name = "phone_number",
        nullable = false
    )
    private String phoneNumber;

    @Column(
        name = "first_name",
        nullable = false
    )
    private String firstName;

    @Column(
        name = "middle_name"
    )
    private String middleName;

    @Column(
        name = "last_name",
        nullable = false
    )
    private String lastName;

    @Column(
        name = "wallet_balance",
        precision = 19,
        scale = 4,
        nullable = false,
        columnDefinition = "DECIMAL(19, 4) DEFAULT 0.0000"
    )
    private BigDecimal walletBalance;


}