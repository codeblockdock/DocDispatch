package online.anshu.docdispatch.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "predicted_diseases")
public class PredictedDisease {
    
    @Id
    private String queryId;
    
    private String symptoms;
    
    private String disease;
    
    private double probability;
    
    // Constructors
    public PredictedDisease() {}
    
    public PredictedDisease(String queryId, String symptoms, String disease) {
        this.queryId = queryId;
        this.symptoms = symptoms;
        this.disease = disease;
    }
    
    public PredictedDisease(String queryId, String symptoms, String disease, double probability) {
        this.queryId = queryId;
        this.symptoms = symptoms;
        this.disease = disease;
        this.probability = probability;
    }
    
    // Getters and Setters
    public String getQueryId() { return queryId; }
    public void setQueryId(String queryId) { this.queryId = queryId; }
    
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    
    public String getDisease() { return disease; }
    public void setDisease(String disease) { this.disease = disease; }
    
    public double getProbability() { return probability; }
    public void setProbability(double probability) { this.probability = probability; }
}
