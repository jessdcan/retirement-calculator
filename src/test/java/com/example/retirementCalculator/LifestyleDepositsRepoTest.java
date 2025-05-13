package com.example.retirementCalculator;

import com.example.retirementCalculator.persistance.entities.LifestyleDepositsEntity;
import com.example.retirementCalculator.persistance.repositories.LifestyleDepositsRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LifestyleDepositsRepoTest {

    @Autowired
    private LifestyleDepositsRepo repo;

    @Test
    void testSaveAndFindById() {

        LifestyleDepositsEntity entity = LifestyleDepositsEntity.builder()
                .lifestyleType("modest")
                .monthlyDeposit(new BigDecimal("1000.00"))
                .annualExpenses(new BigDecimal("12000.00"))
                .description("Basic lifestyle")
                .build();

        LifestyleDepositsEntity saved = repo.save(entity);
        Optional<LifestyleDepositsEntity> found = repo.findById(saved.getId().longValue());

        assertThat(found).isPresent();
        assertThat(found.get().getLifestyleType()).isEqualTo("modest");
    }
}
