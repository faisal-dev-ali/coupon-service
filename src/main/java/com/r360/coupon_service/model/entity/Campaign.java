package com.r360.coupon_service.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "campaigns")
@Data
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    // ✅ Timezone-safe timestamps
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime startAt;
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime endAt;

    private Boolean active;

    @Column(columnDefinition = "json")
    private String meta;
}
