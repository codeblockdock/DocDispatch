package main.java.online.ppriyanshu26.docdispatch.repository;

import main.java.online.ppriyanshu26.docdispatch.entity.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryRepository extends JpaRepository<Query, Integer> {
    List<Query> findByContact(String contact);
}
