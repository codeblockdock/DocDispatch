/*
 * PredictedDiseaseRepository.java - Data Access for Predicted Disease Records
 * 
 * PURPOSE:
 * Spring Data JPA repository for CRUD operations on predicted_disease table.
 */
package online.ppriyanshu26.docdispatch.repository;

import online.ppriyanshu26.docdispatch.entity.PredictedDisease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PredictedDiseaseRepository extends JpaRepository<PredictedDisease, Integer> {
    Optional<PredictedDisease> findByQid(int qid);
}
