package com.bitsbids.bitsbids.Bids;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import com.bitsbids.bitsbids.AnonymousUser.AnonymousUser;
import com.bitsbids.bitsbids.Product.Product;
import com.bitsbids.bitsbids.Users.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "bids")
public class Bids {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "bid_id", nullable = false, updatable = false)
    private UUID bidID;

    @OneToOne
    @JoinColumn(name = "anon_bidder", referencedColumnName = "anon_user_id")
    private AnonymousUser bidderAnonymous;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "product", referencedColumnName = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @Column(name = "bid_amount", nullable = false)
    private BigDecimal bidAmount;

    @Column(name = "bid_time", nullable = false)
    private LocalDateTime bidTime;

}
