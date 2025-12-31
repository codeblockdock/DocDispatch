package online.anshu.docdispatch.repository;

import online.anshu.docdispatch.entity.PredictedDisease;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PredictedDiseaseRepository extends MongoRepository<PredictedDisease, String> {
    Optional<PredictedDisease> findByQueryId(String queryId);
}
