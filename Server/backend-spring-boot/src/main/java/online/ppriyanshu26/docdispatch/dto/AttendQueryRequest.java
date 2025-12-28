package main.java.online.ppriyanshu26.docdispatch.dto;

public class AttendQueryRequest {
    private Integer qid;
    private String contact;
    private String doctor;
    private String treatment;
    private String remarks;
    
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
}
