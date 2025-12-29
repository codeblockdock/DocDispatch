/*
 * PredictDiseaseRequest.java - Data Transfer Object for Disease Prediction Input
 * 
 * PURPOSE:
 * This DTO represents the JSON data sent from the Flutter app when requesting
 * disease prediction based on patient symptoms.
 * 
 * FIELDS:
 * - symptoms: List of symptom strings entered by the patient
 *   Example: ["fever", "cough", "headache"]
 * 
 * HTTP REQUEST EXAMPLE:
 * POST /api/predict
 * Content-Type: application/json
 * {
 *   "symptoms": ["fever", "cough", "fatigue", "body ache"]
 * }
 * 
 * FLOW:
 * Mobile App -> JSON -> Controller -> DTO -> PredictionService -> Python ML Model
 * 
 * ML INTEGRATION:
 * This data is passed to a Python script (predict.py) which uses a trained
 * machine learning model (disease_model.pkl) to predict possible diseases.
 */
package online.ppriyanshu26.docdispatch.dto;

import java.util.List;

public class PredictDiseaseRequest {
    private List<String> symptoms;
    
    public List<String> getSymptoms() { 
        return symptoms; 
    }
    
    public void setSymptoms(List<String> symptoms) { 
        this.symptoms = symptoms; 
    }
}
