package online.anshu.docdispatch.repository;

import online.anshu.docdispatch.entity.PatientLocation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientLocationRepository extends MongoRepository<PatientLocation, String> {
    Optional<PatientLocation> findByQueryId(String queryId);
}
