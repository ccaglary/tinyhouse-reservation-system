package com.tinyhouse.entity;

import com.tinyhouse.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "email")},
        indexes = {@Index(name = "idx_user_email", columnList = "email"), @Index(name = "idx_user_role", columnList = "role")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User extends BaseEntity {
    @Column(name = "first_name", nullable = false, length = 50) private String firstName;
    @Column(name = "last_name", nullable = false, length = 50) private String lastName;
    @Column(name = "email", nullable = false, unique = true, length = 100) private String email;
    @Column(name = "password", nullable = false) private String password;
    @Column(name = "phone_number", length = 20) private String phoneNumber;
    @Enumerated(EnumType.STRING) @Column(name = "role", nullable = false, length = 20) private Role role;
    @Column(name = "active", nullable = false) @Builder.Default private boolean active = true;
    @Column(name = "profile_image") private String profileImage;
    @Column(name = "email_verified", nullable = false) @Builder.Default private boolean emailVerified = false;
    @Column(name = "refresh_token") private String refreshToken;
    @Column(name = "verification_token") private String verificationToken;
    @Column(name = "reset_token") private String resetToken;
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true) @Builder.Default private List<Reservation> reservations = new ArrayList<>();
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true) @Builder.Default private List<Review> reviews = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true) @Builder.Default private List<Notification> notifications = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true) @Builder.Default private List<Favorite> favorites = new ArrayList<>();
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true) @Builder.Default private List<TinyHouse> ownedTinyHouses = new ArrayList<>();
}
