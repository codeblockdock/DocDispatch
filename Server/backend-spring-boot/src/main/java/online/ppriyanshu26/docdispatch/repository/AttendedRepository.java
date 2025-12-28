package main.java.online.ppriyanshu26.docdispatch.repository;

import main.java.online.ppriyanshu26.docdispatch.entity.Attended;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttendedRepository extends JpaRepository<Attended, Integer> {
    Optional<Attended> findByQid(Integer qid);
}
