/*
 * QueryRepository.java - Data Access Layer for Query Entity
 * 
 * PURPOSE:
 * This repository interface provides database access methods for the Query entity.
 * Spring Data JPA automatically implements these methods at runtime.
 * 
 * INHERITED METHODS (from JpaRepository):
 * - save(Query) - Insert or update a query
 * - findById(Integer) - Find query by ID
 * - findAll() - Get all queries
 * - delete(Query) - Delete a query
 * - count() - Count total queries
 * And many more CRUD operations...
 * 
 * CUSTOM METHODS:
 * - findByContact(String) - Retrieves all queries for a specific phone number
 *   Spring Data JPA auto-generates SQL: SELECT * FROM queries WHERE contact = ?
 * 
 * HOW IT WORKS:
 * You only define the method signature - Spring generates the implementation!
 * Method names follow conventions: findBy[FieldName] creates WHERE clauses
 * 
 * USAGE:
 * Service layer injects this repository and calls methods to interact with database
 * No SQL needed - JPA handles everything!
 */
package online.ppriyanshu26.docdispatch.repository;

import online.ppriyanshu26.docdispatch.entity.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryRepository extends JpaRepository<Query, Integer> {
    List<Query> findByContact(String contact);
}
