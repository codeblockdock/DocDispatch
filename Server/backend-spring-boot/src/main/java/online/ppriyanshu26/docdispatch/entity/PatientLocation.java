/*
 * PatientLocation.java - JPA Entity representing Patient Location Information
 * 
 * PURPOSE:
 * This entity class maps to the 'patient_location' table in MySQL database.
 * It stores the address details (pincode, city, state) for each patient query.
 * 
 * DATABASE TABLE: patient_location
 * 
 * FIELDS:
 * - qid: Query ID (Primary Key, Foreign Key to queries table)
 * - pincode: Patient's postal code (10 chars)
 * - city: Patient's city (100 chars)
 * - state: Patient's state (100 chars)
 * 
 * RELATIONSHIP:
 * One-to-One with Query entity via qid (query ID)
 */
package online.ppriyanshu26.docdispatch.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "patient_location")
public class PatientLocation {
    
    @Id
    @Column(nullable = false)
    private int qid;
    
    @Column(nullable = false, length = 10)
    private String pincode;
    
    @Column(nullable = false, length = 100)
    private String city;
    
    @Column(nullable = false, length = 100)
    private String state;
    
    // Constructors
    public PatientLocation() {}
    
    public PatientLocation(int qid, String pincode, String city, String state) {
        this.qid = qid;
        this.pincode = pincode;
        this.city = city;
        this.state = state;
    }
    
    // Getters and Setters
    public int getQid() { return qid; }
    public void setQid(int qid) { this.qid = qid; }
    
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}
