package com.bitsbids.bitsbids.Bids;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import com.bitsbids.bitsbids.AnonymousUser.AnonymousUser;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "bids", uniqueConstraints = {
        @UniqueConstraint(name = "bid_id", columnNames = { "bid_id" }),
        @UniqueConstraint(name = "bidder_anonymous_id", columnNames = { "bidder_anonymous_id" }),
})

public class Bids {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "bid_id", nullable = false, updatable = false)
    private UUID bidID;

    @ManyToOne
    @JoinColumn(name = "anon_seller_id", referencedColumnName = "anon_user_id", nullable = false)
    private AnonymousUser bidderAnonymousID;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id", nullable = false)
    private AnonymousUser productID;

    @Column(name = "bid_amount", nullable = false)
    private BigDecimal bidAmount;

    @Column(name = "bid_time", nullable = false)
    private LocalDateTime bidTime;

    @Column(name = "bid_status", nullable = false)
    private String bidStatus;

}
