package online.anshu.docdispatch.dto;

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
