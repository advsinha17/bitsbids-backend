package com.bitsbids.bitsbids.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import com.bitsbids.bitsbids.AnonymousUser.AnonymousUser;
import com.bitsbids.bitsbids.Bids.Bids;
import com.bitsbids.bitsbids.Users.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(name = "unique_anon_seller", columnNames = { "anon_seller" })
})
public class Product {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "product_id", nullable = false, updatable = false)
    private UUID productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_description", nullable = false)
    private String productDescription;

    @Column(name = "starting_price", nullable = false)
    private BigDecimal startingPrice;

    @JsonManagedReference
    @OneToOne
    @JoinColumn(name = "anon_seller", referencedColumnName = "anon_user_id", nullable = true)
    private AnonymousUser anonymousSeller;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @Column(name = "latest_bid_amount")
    private BigDecimal latestBidAmount;

    @OneToOne
    @JoinColumn(name = "latest_bid", referencedColumnName = "bid_id")
    private Bids latestBid;

    @Column(name = "bid_closing_time", nullable = false)
    private LocalDateTime bidClosingTime;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

    @Column(name = "product_quality")
    private String productQuality;

    @Column(name = "number_of_bids", nullable = false)
    private int numberOfBids;

    public enum ProductStatus {
        ACTIVE,
        UNSOLD,
        SOLD,
        WITHDRAWN
    }

    @Column(name = "product_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    @ElementCollection
    @CollectionTable(name = "product_media", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "media_url")
    private List<String> mediaUrls = new ArrayList<>();

    public enum ProductCategory {
        CLOTHING,
        JEWELLERY,
        EDUCATIONAL,
        ROOM_ACCESSORIES,
        ELECTRONICS,
        SPORTS,
        ESSENTIALS
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Set<ProductCategory> categories = new HashSet<>();

}
