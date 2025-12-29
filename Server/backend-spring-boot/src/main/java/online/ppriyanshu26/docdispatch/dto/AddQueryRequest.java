package online.ppriyanshu26.docdispatch.dto;

public class AddQueryRequest {
    private String contact;
    private String name;
    private int age;
    private String gender;
    private int temperature;
    private int days;
    private String contagious;
    
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
}
