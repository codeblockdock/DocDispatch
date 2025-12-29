/*
 * DiseasePrediction.java - Data Transfer Object for Single Disease Prediction Result
 * 
 * PURPOSE:
 * This DTO represents a single disease prediction with its probability score.
 * Multiple instances of this class are returned for the top predicted diseases.
 * 
 * FIELDS:
 * - disease: Name of the predicted disease (e.g., "Common Cold", "Influenza")
 * - probability: Confidence percentage (0-100) that patient has this disease
 * 
 * EXAMPLE:
 * {
 *   "disease": "Common Cold",
 *   "probability": 85.67
 * }
 * 
 * USAGE:
 * The Python ML model returns top 3 diseases with highest probabilities.
 * Each prediction is converted to this DTO and sent back to the mobile app.
 * 
 * FLOW:
 * Python Model -> JSON -> PredictionService -> DTO -> Controller -> Mobile App
 */
package online.ppriyanshu26.docdispatch.dto;

public class DiseasePrediction {
    private String disease;
    private double probability;
    
    public DiseasePrediction() {}
    
    public DiseasePrediction(String disease, double probability) {
        this.disease = disease;
        this.probability = probability;
    }
    
    public String getDisease() { 
        return disease; 
    }
    
    public void setDisease(String disease) { 
        this.disease = disease; 
    }
    
    public double getProbability() { 
        return probability; 
    }
    
    public void setProbability(double probability) { 
        this.probability = probability; 
    }
}
