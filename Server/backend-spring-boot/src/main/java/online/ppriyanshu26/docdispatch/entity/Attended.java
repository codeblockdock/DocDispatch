/*
 * Attended.java - JPA Entity representing Doctor Responses to Patient Queries
 * 
 * PURPOSE:
 * This entity class maps to the 'attended' table in MySQL database.
 * It stores doctor's responses, diagnoses, and treatments for patient queries.
 * 
 * DATABASE TABLE: attended
 * 
 * FIELDS:
 * - qid: Query ID (Primary Key, references queries table)
 * - contact: Doctor's phone number for patient follow-up
 * - doctor: Name of the attending doctor
 * - treatment: Prescribed treatment and medication details
 * - remarks: Additional notes or instructions from doctor (optional)
 * - attendedAt: Timestamp when doctor responded to query
 * 
 * RELATIONSHIP:
 * One-to-One with Query entity (each attended record links to one query)
 * The qid serves as both primary key and foreign key
 * 
 * LIFECYCLE:
 * When a query is attended:
 * 1. Query.attended field is set to 1
 * 2. New Attended record is created with doctor's response
 * 3. attendedAt timestamp is auto-set via @PrePersist
 * 
 * JPA ANNOTATIONS:
 * @Entity - Marks as database entity
 * @Table - Maps to 'attended' table
 * @Id - qid is the primary key (not auto-generated)
 * @PrePersist - Auto-sets attendedAt timestamp before first save
 */
package online.ppriyanshu26.docdispatch.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attended")
public class Attended {
    
    @Id
    @Column(nullable = false)
    private int qid;
    
    @Column(nullable = false, length = 15)
    private String contact;
    
    @Column(nullable = false, length = 255)
    private String doctor;
    
    @Column(nullable = false, length = 255)
    private String hospital;
    
    @Column(nullable = false, length = 100)
    private String city;
    
    @Column(nullable = false, length = 255)
    private String diagnosis;
    
    @Column(nullable = false, length = 255)
    private String treatment;
    
    @Column(length = 255)
    private String advice;
    
    @Column(nullable = false, length = 50)
    private String appointment;
    
    @Column(name = "attended_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime attendedAt;
    
    @PrePersist
    protected void onCreate() {
        if (attendedAt == null) {
            attendedAt = LocalDateTime.now();
        }
    }
    
    // Getters and Setters
    public int getQid() { return qid; }
    public void setQid(int qid) { this.qid = qid; }
    
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    
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
    
    public LocalDateTime getAttendedAt() { return attendedAt; }
    public void setAttendedAt(LocalDateTime attendedAt) { this.attendedAt = attendedAt; }
}
