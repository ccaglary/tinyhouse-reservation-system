package com.tinyhouse.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "reviews", uniqueConstraints = {@UniqueConstraint(columnNames = {"tenant_id", "tiny_house_id"})})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Review extends BaseEntity {
    @Column(name = "rating", nullable = false) private BigDecimal rating;
    @Column(name = "comment", columnDefinition = "TEXT") private String comment;
    @Column(name = "owner_reply", columnDefinition = "TEXT") private String ownerReply;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "tiny_house_id", nullable = false) private TinyHouse tinyHouse;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "tenant_id", nullable = false) private User tenant;
}
