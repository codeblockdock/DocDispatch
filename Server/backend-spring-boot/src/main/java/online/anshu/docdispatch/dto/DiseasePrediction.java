package online.anshu.docdispatch.dto;

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
