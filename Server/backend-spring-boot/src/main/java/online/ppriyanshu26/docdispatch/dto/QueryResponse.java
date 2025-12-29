/*
 * QueryResponse.java - Data Transfer Object for Returning Query Data to Client
 * 
 * PURPOSE:
 * This DTO represents the JSON data sent back to the Flutter mobile app when
 * a patient requests their query history. It combines data from both Query and
 * Attended entities.
 * 
 * FIELDS (sent to mobile app):
 * - qid: Unique query ID
 * - contact: Patient's phone number
 * - attended: Status (0=pending, 1=doctor responded)
 * - doctor: Name of doctor who attended (null if not attended)
 * - treatment: Prescribed treatment (null if not attended)
 * - remarks: Doctor's additional notes (null if not attended)
 * - attendedAt: Timestamp when doctor responded (null if not attended)
 * 
 * HTTP RESPONSE EXAMPLE:
 * GET /api/queries?contact=9876543210
 * Response: 200 OK
 * [
 *   {
 *     "qid": 123,
 *     "contact": "9876543210",
 *     "attended": 1,
 *     "doctor": "Dr. Smith",
 *     "treatment": "Paracetamol 500mg",
 *     "remarks": "Rest for 3 days",
 *     "attendedAt": "2025-12-29 10:30:00"
 *   },
 *   {
 *     "qid": 124,
 *     "contact": "9876543210",
 *     "attended": 0,
 *     "doctor": null,
 *     "treatment": null,
 *     "remarks": null,
 *     "attendedAt": null
 *   }
 * ]
 * 
 * FLOW:
 * Database -> Entities -> Service (combines data) -> DTO -> Controller -> JSON -> Mobile App
 */
package online.ppriyanshu26.docdispatch.dto;

public class QueryResponse {
    private int qid;
    private String contact;
    private int attended;
    private String doctor;
    private String treatment;
    private String remarks;
    private String attendedAt;
    
    public int getQid() { return qid; }
    public void setQid(int qid) { this.qid = qid; }
    
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    
    public int getAttended() { return attended; }
    public void setAttended(int attended) { this.attended = attended; }
    
    public String getDoctor() { return doctor; }
    public void setDoctor(String doctor) { this.doctor = doctor; }
    
    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    public String getAttendedAt() { return attendedAt; }
    public void setAttendedAt(String attendedAt) { this.attendedAt = attendedAt; }
}
