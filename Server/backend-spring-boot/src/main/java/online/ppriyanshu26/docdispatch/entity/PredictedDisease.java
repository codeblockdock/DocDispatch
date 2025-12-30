/*
 * PredictedDisease.java - JPA Entity representing Disease Predictions
 * 
 * PURPOSE:
 * This entity class maps to the 'predicted_disease' table in MySQL database.
 * It stores the ML model's disease prediction when a patient submits more than 5 symptoms.
 * 
 * DATABASE TABLE: predicted_disease
 * 
 * FIELDS:
 * - qid: Query ID (Primary Key, foreign key reference to queries table)
 * - symptoms: JSON array of symptoms used for prediction
 * - disease: The top predicted disease from the ML model
 */
package online.ppriyanshu26.docdispatch.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "predicted_disease")
public class PredictedDisease {
    
    @Id
    @Column(nullable = false)
    private int qid;
    
    @Column(nullable = false, columnDefinition = "JSON")
    private String symptoms;
    
    @Column(nullable = false, length = 255)
    private String disease;
    
    // Constructors
    public PredictedDisease() {}
    
    public PredictedDisease(int qid, String symptoms, String disease) {
        this.qid = qid;
        this.symptoms = symptoms;
        this.disease = disease;
    }
    
    // Getters and Setters
    public int getQid() { return qid; }
    public void setQid(int qid) { this.qid = qid; }
    
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    
    public String getDisease() { return disease; }
    public void setDisease(String disease) { this.disease = disease; }
}
