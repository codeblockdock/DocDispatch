package online.anshu.docdispatch.dto;

public class HospitalLoginRequest {
    private String hospitalId;
    private String password;
    
    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
