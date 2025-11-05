package com.r360.coupon_service.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_segments")
@Data
public class UserSegment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(columnDefinition = "json")
    private String criteriaJson;
}
