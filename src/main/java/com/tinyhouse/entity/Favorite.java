package com.tinyhouse.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "favorites", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "tiny_house_id"})})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Favorite extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "tiny_house_id", nullable = false) private TinyHouse tinyHouse;
}
