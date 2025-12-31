package online.anshu.docdispatch.repository;

import online.anshu.docdispatch.entity.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalRepository extends MongoRepository<Hospital, String> {
    Optional<Hospital> findByHospitalId(String hospitalId);
    Optional<Hospital> findByHospitalIdAndPassword(String hospitalId, String password);
    boolean existsByHospitalId(String hospitalId);
}
