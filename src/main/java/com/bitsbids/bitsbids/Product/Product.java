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

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(name = "unique_anon_seller_id", columnNames = { "anon_seller_id" })
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

    @ManyToOne
    @JoinColumn(name = "anon_seller_id", referencedColumnName = "anon_user_id", nullable = false)
    private AnonymousUser anonymousSeller;

    @Column(name = "latest_bid_amount")
    private BigDecimal latestBidAmount;

    @Column(name = "latest_bid_user")
    private String latestBidUser;

    @Column(name = "bid_closing_time", nullable = false)
    private LocalDateTime bidClosingTime;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

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

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "product_categories", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

}
