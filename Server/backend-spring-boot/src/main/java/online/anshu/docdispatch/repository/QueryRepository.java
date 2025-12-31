package online.anshu.docdispatch.repository;

import online.anshu.docdispatch.entity.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryRepository extends MongoRepository<Query, String> {
    List<Query> findByContact(String contact);
}
