package main.java.online.ppriyanshu26.docdispatch.dto;

public class AddQueryRequest {
    private String contact;
    private String name;
    private Integer age;
    private String gender;
    private Integer temperature;
    private Integer days;
    private String contagious;
    
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public Integer getTemperature() { return temperature; }
    public void setTemperature(Integer temperature) { this.temperature = temperature; }
    
    public Integer getDays() { return days; }
    public void setDays(Integer days) { this.days = days; }
    
    public String getContagious() { return contagious; }
    public void setContagious(String contagious) { this.contagious = contagious; }
}
