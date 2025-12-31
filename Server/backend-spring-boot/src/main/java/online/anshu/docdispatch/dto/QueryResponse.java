package online.anshu.docdispatch.dto;

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
