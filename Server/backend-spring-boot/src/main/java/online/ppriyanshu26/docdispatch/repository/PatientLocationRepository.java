/*
 * PatientLocationRepository.java - Spring Data JPA Repository for PatientLocation
 * 
 * PURPOSE:
 * This repository provides database access methods for the PatientLocation entity.
 * It extends JpaRepository to automatically provide CRUD operations and custom query methods.
 * 
 * METHODS PROVIDED BY JPAREPOSITORY:
 * - save(entity) - Insert or update
 * - findById(id) - Get by primary key
 * - findAll() - Get all records
 * - delete(entity) - Delete a record
 * - deleteById(id) - Delete by primary key
 * 
 * CUSTOM METHODS:
 * Can be added with @Query annotation for complex queries
 */
package online.ppriyanshu26.docdispatch.repository;

import online.ppriyanshu26.docdispatch.entity.PatientLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientLocationRepository extends JpaRepository<PatientLocation, Integer> {
    // findById(id) is inherited from JpaRepository
    // save(entity) is inherited from JpaRepository
    // deleteById(id) is inherited from JpaRepository
    
    Optional<PatientLocation> findByQid(int qid);
}
