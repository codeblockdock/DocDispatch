package online.anshu.docdispatch.dto;

import java.util.List;

public class MassAttendRequest {
    private List<String> queryIds;
    private String doctor;
    private String hospital;
    private String city;
    private String state;
    private String diagnosis;
    private String treatment;
    private String advice;
    private String appointment;
    private int doctorsDispatched;
    private String location;
    private String pincode;
    
    public List<String> getQueryIds() { return queryIds; }
    public void setQueryIds(List<String> queryIds) { this.queryIds = queryIds; }
    
    public String getDoctor() { return doctor; }
    public void setDoctor(String doctor) { this.doctor = doctor; }
    
    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    
    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }
    
    public String getAdvice() { return advice; }
    public void setAdvice(String advice) { this.advice = advice; }
    
    public String getAppointment() { return appointment; }
    public void setAppointment(String appointment) { this.appointment = appointment; }
    
    public int getDoctorsDispatched() { return doctorsDispatched; }
    public void setDoctorsDispatched(int doctorsDispatched) { this.doctorsDispatched = doctorsDispatched; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
}
