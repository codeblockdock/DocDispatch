package online.anshu.docdispatch.dto;

public class PatientDataRequest {
    private String name;
    private String symptoms;
    private String predictedDisease;
    private int probability;
    private String locationId;
    private String contact;
    private int age;
    private String gender;
    private int temperature;
    private int days;
    private double riskfactor;
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    
    public String getPredictedDisease() { return predictedDisease; }
    public void setPredictedDisease(String predictedDisease) { this.predictedDisease = predictedDisease; }
    
    public int getProbability() { return probability; }
    public void setProbability(int probability) { this.probability = probability; }
    
    public String getLocationId() { return locationId; }
    public void setLocationId(String locationId) { this.locationId = locationId; }
    
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public int getTemperature() { return temperature; }
    public void setTemperature(int temperature) { this.temperature = temperature; }
    
    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }
    
    public double getRiskfactor() { return riskfactor; }
    public void setRiskfactor(double riskfactor) { this.riskfactor = riskfactor; }
}
