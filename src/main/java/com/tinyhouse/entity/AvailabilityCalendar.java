package com.tinyhouse.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity @Table(name = "availability_calendar", uniqueConstraints = {@UniqueConstraint(columnNames = {"tiny_house_id", "available_date"})})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AvailabilityCalendar extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "tiny_house_id", nullable = false) private TinyHouse tinyHouse;
    @Column(name = "available_date", nullable = false) private LocalDate availableDate;
    @Column(name = "available", nullable = false) @Builder.Default private boolean available = true;
}
