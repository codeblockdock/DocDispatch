package online.anshu.docdispatch.dto;

public class PatientDashboardDto {
    private String id;
    private String name;
    private String symptoms;
    private String predictedDisease;
    private int probability;
    private String city;
    private String state;
    private String pincode;
    private String status;
    private String contact;
    private int age;
    private String gender;
    private int temperature;
    private int days;
    private double riskfactor;
    private String village;
    private String receivedAt;
    private int attended;
    private String doctor;
    private String hospital;
    private String diagnosis;
    private String treatment;
    private String advice;
    private String appointment;
    private String attendedTimestamp;
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    
    public String getPredictedDisease() { return predictedDisease; }
    public void setPredictedDisease(String predictedDisease) { this.predictedDisease = predictedDisease; }
    
    public int getProbability() { return probability; }
    public void setProbability(int probability) { this.probability = probability; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
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
    
    public String getReceivedAt() { return receivedAt; }
    public void setReceivedAt(String receivedAt) { this.receivedAt = receivedAt; }
    
    public int getAttended() { return attended; }
    public void setAttended(int attended) { this.attended = attended; }
    
    public String getDoctor() { return doctor; }
    public void setDoctor(String doctor) { this.doctor = doctor; }
    
    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }
    
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    
    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }
    
    public String getAdvice() { return advice; }
    public void setAdvice(String advice) { this.advice = advice; }
    
    public String getAppointment() { return appointment; }
    public void setAppointment(String appointment) { this.appointment = appointment; }
    
    public String getAttendedTimestamp() { return attendedTimestamp; }
    public void setAttendedTimestamp(String attendedTimestamp) { this.attendedTimestamp = attendedTimestamp; }
}
