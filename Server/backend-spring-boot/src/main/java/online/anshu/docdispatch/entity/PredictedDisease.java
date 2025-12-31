package online.anshu.docdispatch.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "predicted_diseases")
public class PredictedDisease {
    
    @Id
    private String queryId;
    
    private String symptoms;
    
    private String disease;
    
    // Constructors
    public PredictedDisease() {}
    
    public PredictedDisease(String queryId, String symptoms, String disease) {
        this.queryId = queryId;
        this.symptoms = symptoms;
        this.disease = disease;
    }
    
    // Getters and Setters
    public String getQueryId() { return queryId; }
    public void setQueryId(String queryId) { this.queryId = queryId; }
    
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    
    public String getDisease() { return disease; }
    public void setDisease(String disease) { this.disease = disease; }
}
