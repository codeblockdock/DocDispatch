package online.anshu.docdispatch.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "patient_locations")
public class PatientLocation {
    
    @Id
    private String queryId;
    
    private String pincode;
    
    private String city;

    private String village;
    
    private String state;
    
    // Constructors
    public PatientLocation() {}
    
    public PatientLocation(String queryId, String pincode, String city, String village, String state) {
        this.queryId = queryId;
        this.pincode = pincode;
        this.city = city;
        this.village = village;
        this.state = state;
    }
    
    // Getters and Setters
    public String getQueryId() { return queryId; }
    public void setQueryId(String queryId) { this.queryId = queryId; }
    
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}
