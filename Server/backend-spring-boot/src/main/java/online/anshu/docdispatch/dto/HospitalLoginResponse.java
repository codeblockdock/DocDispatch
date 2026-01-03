package online.anshu.docdispatch.dto;

public class HospitalLoginResponse {
    private String token;
    private String hospitalId;
    private String name;
    private String state;
    private String city;
    private String message;
    private boolean success;
    private boolean admin;
    
    public HospitalLoginResponse() {}
    
    public HospitalLoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }
}
