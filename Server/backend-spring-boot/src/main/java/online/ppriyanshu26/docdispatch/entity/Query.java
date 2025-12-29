/*
 * Query.java - JPA Entity representing Patient Medical Queries
 * 
 * PURPOSE:
 * This entity class maps to the 'queries' table in MySQL database.
 * It represents a patient's medical query submitted through the mobile app.
 * 
 * DATABASE TABLE: queries
 * 
 * FIELDS:
 * - qid: Auto-generated unique query ID (Primary Key)
 * - contact: Patient's phone number (15 chars max)
 * - name: Patient's full name (255 chars)
 * - age: Patient's age (0-255)
 * - gender: Male/Female (6 chars)
 * - temperature: Body temperature in degrees
 * - days: Number of days patient has been sick
 * - contagious: Yes/No - if disease is contagious
 * - treatment: Prescribed treatment (100 chars, optional)
 * - disease: Diagnosed disease (20 chars, optional)
 * - attended: Status flag (0=pending, 1=attended by doctor)
 * - receivedAt: Timestamp when query was submitted
 * 
 * JPA ANNOTATIONS:
 * @Entity - Marks this as a database entity
 * @Table - Maps to 'queries' table
 * @Id - Primary key field
 * @GeneratedValue - Auto-increment for primary key
 * @Column - Specifies database column constraints
 * @PrePersist - Sets receivedAt timestamp before saving
 * 
 * RELATIONSHIPS:
 * One-to-One with Attended entity (if query is attended)
 */
package online.ppriyanshu26.docdispatch.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "queries")
public class Query {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private int qid;
    
    @Column(nullable = false, length = 15)
    private String contact;
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @Column(nullable = false, columnDefinition = "TINYINT UNSIGNED")
    private int age;
    
    @Column(nullable = false, length = 6)
    private String gender;
    
    @Column(nullable = false, columnDefinition = "TINYINT")
    private int temperature;
    
    @Column(nullable = false, columnDefinition = "TINYINT")
    private int days;
    
    @Column(nullable = false, length = 3)
    private String contagious;
    
    @Column(length = 100)
    private String treatment;
    
    @Column(length = 20)
    private String disease;
    
    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private int attended = 0;
    
    @Column(name = "received_at", nullable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime receivedAt;
    
    @PrePersist
    protected void onCreate() {
        if (receivedAt == null) {
            receivedAt = LocalDateTime.now();
        }
    }
    
    // Getters and Setters
    public int getQid() { return qid; }
    public void setQid(int qid) { this.qid = qid; }
    
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public int getTemperature() { return temperature; }
    public void setTemperature(int temperature) { this.temperature = temperature; }
    
    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }
    
    public String getContagious() { return contagious; }
    public void setContagious(String contagious) { this.contagious = contagious; }
    
    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }
    
    public String getDisease() { return disease; }
    public void setDisease(String disease) { this.disease = disease; }
    
    public int getAttended() { return attended; }
    public void setAttended(int attended) { this.attended = attended; }
    
    public LocalDateTime getReceivedAt() { return receivedAt; }
    public void setReceivedAt(LocalDateTime receivedAt) { this.receivedAt = receivedAt; }
}
