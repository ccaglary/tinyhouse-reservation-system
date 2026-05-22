package com.tinyhouse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "tiny_houses", indexes = {@Index(name = "idx_th_city", columnList = "city"),
        @Index(name = "idx_th_active", columnList = "active"), @Index(name = "idx_th_price", columnList = "nightly_price"),
        @Index(name = "idx_th_owner", columnList = "owner_id")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TinyHouse extends BaseEntity {
    @Column(name = "title", nullable = false, length = 150) private String title;
    @Column(name = "description", nullable = false, columnDefinition = "TEXT") private String description;
    @Column(name = "city", nullable = false, length = 50) private String city;
    @Column(name = "district", length = 50) private String district;
    @Column(name = "address", nullable = false) private String address;
    @Column(name = "latitude") private Double latitude;
    @Column(name = "longitude") private Double longitude;
    @Column(name = "nightly_price", nullable = false, precision = 10, scale = 2) private BigDecimal nightlyPrice;
    @Column(name = "cleaning_fee", precision = 10, scale = 2) @Builder.Default private BigDecimal cleaningFee = BigDecimal.ZERO;
    @Column(name = "capacity", nullable = false) private Integer capacity;
    @Column(name = "room_count", nullable = false) @Builder.Default private Integer roomCount = 1;
    @Column(name = "bed_count", nullable = false) @Builder.Default private Integer bedCount = 1;
    @Column(name = "bathroom_count", nullable = false) @Builder.Default private Integer bathroomCount = 1;
    @Column(name = "wifi") @Builder.Default private boolean wifi = false;
    @Column(name = "parking") @Builder.Default private boolean parking = false;
    @Column(name = "air_conditioner") @Builder.Default private boolean airConditioner = false;
    @Column(name = "pet_allowed") @Builder.Default private boolean petAllowed = false;
    @Column(name = "average_rating", precision = 3, scale = 2) @Builder.Default private BigDecimal averageRating = BigDecimal.ZERO;
    @Column(name = "active", nullable = false) @Builder.Default private boolean active = true;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "owner_id", nullable = false) private User owner;
    @OneToMany(mappedBy = "tinyHouse", cascade = CascadeType.ALL, orphanRemoval = true) @Builder.Default private List<TinyHouseImage> images = new ArrayList<>();
    @OneToMany(mappedBy = "tinyHouse", cascade = CascadeType.ALL, orphanRemoval = true) @Builder.Default private List<AvailabilityCalendar> availabilityCalendars = new ArrayList<>();
    @OneToMany(mappedBy = "tinyHouse", cascade = CascadeType.ALL, orphanRemoval = true) @Builder.Default private List<Reservation> reservations = new ArrayList<>();
    @OneToMany(mappedBy = "tinyHouse", cascade = CascadeType.ALL, orphanRemoval = true) @Builder.Default private List<Review> reviews = new ArrayList<>();
    @OneToMany(mappedBy = "tinyHouse", cascade = CascadeType.ALL, orphanRemoval = true) @Builder.Default private List<Favorite> favorites = new ArrayList<>();
}
