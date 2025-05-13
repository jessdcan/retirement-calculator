package com.example.retirementCalculator.persistance.repositories;

import com.example.retirementCalculator.persistance.entities.LifestyleDepositsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link LifestyleDepositsEntity}.
 * <p>
 * Provides CRUD operations and query method support for the lifestyle_deposits table
 * via Spring Data JPA.
 * </p>
 */
@Repository
public interface LifestyleDepositsRepo extends JpaRepository<LifestyleDepositsEntity, Long> {
    // Custom query methods can be defined here if needed
}
