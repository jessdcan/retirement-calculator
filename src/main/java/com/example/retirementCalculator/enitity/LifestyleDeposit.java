package com.retirement.calculator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entity representing the lifestyle deposit information for retirement planning.
 */
@Entity
@Table(name = "lifestyle_deposits", schema = "staging")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LifestyleDeposit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "lifestyle_type", nullable = false)
    private String lifestyleType;

    @Column(name = "monthly_deposit", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyDeposit;

    @Column(name = "annual_expenses", nullable = false, precision = 12, scale = 2)
    private BigDecimal annualExpenses;

    @Column(name = "description")
    private String description;
}