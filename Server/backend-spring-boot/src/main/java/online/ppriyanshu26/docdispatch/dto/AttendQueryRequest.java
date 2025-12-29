/*
 * AttendQueryRequest.java - Data Transfer Object for Doctor Responding to Patient Query
 * 
 * PURPOSE:
 * This DTO represents the JSON data sent when a doctor attends to a patient's
 * medical query and provides diagnosis and treatment.
 * 
 * FIELDS (sent from doctor's interface/app):
 * - qid: ID of the query being attended
 * - contact: Doctor's contact number for patient follow-up
 * - doctor: Name of the attending doctor
 * - treatment: Prescribed treatment and medications
 * - remarks: Additional notes or instructions (optional)
 * 
 * HTTP REQUEST EXAMPLE:
 * POST /api/attend
 * Content-Type: application/json
 * {
 *   "qid": 123,
 *   "contact": "9999999999",
 *   "doctor": "Dr. Smith",
 *   "treatment": "Take paracetamol 500mg twice daily",
 *   "remarks": "Rest for 3 days. Return if fever persists."
 * }
 * 
 * WHAT HAPPENS:
 * 1. Query status is updated to attended (attended = 1)
 * 2. New record created in 'attended' table with doctor's response
 * 3. Patient can see doctor's response when they check query status
 * 
 * FLOW:
 * Doctor App/Panel -> JSON -> Controller -> DTO -> Service -> Entities -> Database
 */
package online.ppriyanshu26.docdispatch.dto;

public class AttendQueryRequest {
    private int qid;
    private String contact;
    private String doctor;
    private String treatment;
    private String remarks;
    
    public int getQid() { return qid; }
    public void setQid(int qid) { this.qid = qid; }
    
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    
    public String getDoctor() { return doctor; }
    public void setDoctor(String doctor) { this.doctor = doctor; }
    
    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
