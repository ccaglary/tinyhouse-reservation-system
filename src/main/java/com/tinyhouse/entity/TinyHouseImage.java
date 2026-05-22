package com.tinyhouse.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "tiny_house_images") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TinyHouseImage extends BaseEntity {
    @Column(name = "image_url", nullable = false) private String imageUrl;
    @Column(name = "cover_image", nullable = false) @Builder.Default private boolean coverImage = false;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "tiny_house_id", nullable = false) private TinyHouse tinyHouse;
}
