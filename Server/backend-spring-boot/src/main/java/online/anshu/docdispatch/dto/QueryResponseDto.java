package online.anshu.docdispatch.dto;

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
