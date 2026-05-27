package com.westoncodeops.zonixrental.entities;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Builder
@Table(name = "units")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String unitNumber;

    @Column(nullable = false)
    private BigDecimal rentAmount;

    @Column(nullable = false, unique = true)
    private Integer Floor;

    @Column(nullable = false)
    private Boolean isOccupied = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User tenant;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
