/*
 * QueryResponseDto.java - Response DTO for queries list
 * 
 * PURPOSE:
 * Returns query information in the format expected by Flutter app's QueryModel
 * 
 * FIELDS (matches Flutter QueryModel):
 * - name: Patient's name
 * - attended: 0 = pending, 1 = attended
 * - doctor: Doctor's name (or "Pending" if not attended)
 * - hospital: Hospital name (or "Unknown Hospital" if not attended)
 * - city: City name (or "Unknown City" if not attended)
 * - treatment: Treatment details
 * - diagnosis: Diagnosis (or "Under Observation" if not attended)
 * - advice: Doctor's advice
 * - date: Timestamp when query was received
 * - appointment: Appointment details (or "Not Applicable" if not attended)
 */
package online.ppriyanshu26.docdispatch.dto;

public class QueryResponseDto {
    private String name;
    private int attended;
    private String doctor;
    private String hospital;
    private String city;
    private String treatment;
    private String diagnosis;
    private String advice;
    private String date;
    private String appointment;
    
    // Constructors
    public QueryResponseDto() {}
    
    public QueryResponseDto(String name, int attended, String doctor, String hospital, 
                           String city, String treatment, String diagnosis, String advice, 
                           String date, String appointment) {
        this.name = name;
        this.attended = attended;
        this.doctor = doctor;
        this.hospital = hospital;
        this.city = city;
        this.treatment = treatment;
        this.diagnosis = diagnosis;
        this.advice = advice;
        this.date = date;
        this.appointment = appointment;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getAttended() { return attended; }
    public void setAttended(int attended) { this.attended = attended; }
    
    public String getDoctor() { return doctor; }
    public void setDoctor(String doctor) { this.doctor = doctor; }
    
    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }
    
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    
    public String getAdvice() { return advice; }
    public void setAdvice(String advice) { this.advice = advice; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getAppointment() { return appointment; }
    public void setAppointment(String appointment) { this.appointment = appointment; }
}
