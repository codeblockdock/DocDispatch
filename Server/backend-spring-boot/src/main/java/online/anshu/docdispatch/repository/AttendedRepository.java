package online.anshu.docdispatch.repository;

import online.anshu.docdispatch.entity.Attended;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttendedRepository extends MongoRepository<Attended, String> {
    Optional<Attended> findByQueryId(String queryId);
}
