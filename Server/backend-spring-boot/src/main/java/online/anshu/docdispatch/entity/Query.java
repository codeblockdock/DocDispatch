package online.anshu.docdispatch.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.time.ZonedDateTime;
import java.time.ZoneId;

@Document(collection = "queries")
public class Query {
    
    @Id
    private String id;
    
    private String contact;
    
    private String name;
    
    private int age;
    
    private String gender;
    
    private int temperature;
    
    private int days;
    
    private String contagious;
    
    private String symptoms;
    
    private int attended = 0;
    
    private Date receivedAt;

    public Query() {
        this.receivedAt = Date.from(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toInstant());
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public int getTemperature() { return temperature; }
    public void setTemperature(int temperature) { this.temperature = temperature; }
    
    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }
    
    public String getContagious() { return contagious; }
    public void setContagious(String contagious) { this.contagious = contagious; }
    
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    
    public int getAttended() { return attended; }
    public void setAttended(int attended) { this.attended = attended; }
    
    public Date getReceivedAt() { return receivedAt; }
    public void setReceivedAt(Date receivedAt) { this.receivedAt = receivedAt; }
}
