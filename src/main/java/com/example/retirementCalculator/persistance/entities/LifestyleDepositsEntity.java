package com.example.retirementCalculator.persistance.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entity representing the lifestyle deposit information used in retirement planning calculations.
 * <p>
 * Maps to the <code>staging.lifestyle_deposits</code> table in the PostgreSQL database.
 * This entity contains lifestyle type data, associated monthly deposits, and expected annual expenses.
 * </p>
 */
@Entity
@Table(name = "lifestyle_deposits", schema = "staging")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LifestyleDepositsEntity {
    /**
     * Primary key identifier for the lifestyle deposit record.
     * Auto-generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The type or category of lifestyle (e.g., 'modest', 'comfortable', etc.).
     * Cannot be null.
     */
    @NotNull(message = "lifestyleType cannot be null")
    @Column(name = "lifestyle_type", nullable = false)
    private String lifestyleType;

    /**
     * The expected monthly deposit amount for the given lifestyle.
     * Cannot be null.
     */
    @NotNull(message = "monthlyDeposit cannot be null")
    @Column(name = "monthly_deposit", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyDeposit;

    /**
     * Optional description providing additional context or notes about the lifestyle type.
     */
    @Column(name = "description")
    private String description;
}