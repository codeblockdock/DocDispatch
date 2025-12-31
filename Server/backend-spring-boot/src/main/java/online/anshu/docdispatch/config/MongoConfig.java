package online.anshu.docdispatch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class MongoConfig {

    @Autowired
    private MappingMongoConverter mappingMongoConverter;

    @PostConstruct
    public void setUp() {
        // Disable writing _class to MongoDB documents
        mappingMongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
    }
}
