package com.example.retirementCalculator.persistance.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
public class LifestyleDepositsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @NotNull(message =  "lifestyleType cannot be null")
    @Column(name = "lifestyle_type", nullable = false)
    private String lifestyleType;

    @NotNull(message =  "monthlyDeposit cannot be null")
    @Column(name = "monthly_deposit", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyDeposit;

    @NotNull(message =  "annualExpenses cannot be null")
    @Column(name = "annual_expenses", nullable = false, precision = 12, scale = 2)
    private BigDecimal annualExpenses;

    @Column(name = "description")
    private String description;
}