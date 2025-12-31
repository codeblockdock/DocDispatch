package online.anshu.docdispatch.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.time.ZonedDateTime;
import java.time.ZoneId;

@Document(collection = "attended")
public class Attended {
    
    @Id
    private String queryId;
    
    private String doctor;
    
    private String hospital;
    
    private String city;
    
    private String diagnosis;
    
    private String treatment;
    
    private String advice;
    
    private String appointment;
    
    private Date timestamp;

    public Attended() {
        this.timestamp = Date.from(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toInstant());
    }
    
    // Getters and Setters
    public String getQueryId() { return queryId; }
    public void setQueryId(String queryId) { this.queryId = queryId; }
    
    public String getDoctor() { return doctor; }
    public void setDoctor(String doctor) { this.doctor = doctor; }
    
    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }
    
    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    
    public String getAdvice() { return advice; }
    public void setAdvice(String advice) { this.advice = advice; }
    
    public String getAppointment() { return appointment; }
    public void setAppointment(String appointment) { this.appointment = appointment; }
    
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
