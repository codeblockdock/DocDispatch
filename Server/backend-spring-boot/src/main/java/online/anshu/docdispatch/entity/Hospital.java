package online.anshu.docdispatch.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.util.Date;

@Document(collection = "hospitals")
public class Hospital {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String hospitalId;
    
    private String name;
    
    private String password;
    
    private String state;
    
    private String city;
    
    private String address;
    
    private String phone;
    
    private String email;
    
    private boolean active = true;
    
    private boolean admin = false;
    
    private Date lastLogin;
    
    private String token;
    
    private java.util.List<String> region = new java.util.ArrayList<>();

    public Hospital() {
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }
    
    public Date getLastLogin() { return lastLogin; }
    public void setLastLogin(Date lastLogin) { this.lastLogin = lastLogin; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public java.util.List<String> getRegion() { return region; }
    public void setRegion(java.util.List<String> region) { this.region = region; }
}
