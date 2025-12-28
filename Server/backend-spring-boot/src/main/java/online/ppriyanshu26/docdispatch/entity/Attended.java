package main.java.online.ppriyanshu26.docdispatch.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attended")
public class Attended {
    
    @Id
    @Column(nullable = false)
    private Integer qid;
    
    @Column(nullable = false, length = 15)
    private String contact;
    
    @Column(nullable = false, length = 255)
    private String doctor;
    
    @Column(nullable = false, length = 255)
    private String treatment;
    
    @Column(length = 255)
    private String remarks;
    
    @Column(name = "attended_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime attendedAt;
    
    @PrePersist
    protected void onCreate() {
        if (attendedAt == null) {
            attendedAt = LocalDateTime.now();
        }
    }
    
    // Getters and Setters
    public Integer getQid() { return qid; }
    public void setQid(Integer qid) { this.qid = qid; }
    
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    
    public String getDoctor() { return doctor; }
    public void setDoctor(String doctor) { this.doctor = doctor; }
    
    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    public LocalDateTime getAttendedAt() { return attendedAt; }
    public void setAttendedAt(LocalDateTime attendedAt) { this.attendedAt = attendedAt; }
}
