package online.anshu.docdispatch.dto;

public class AddQueryRequest {
    private String contact;
    private String name;
    private int age;
    private String gender;
    private int temperature;
    private int days;
    private String contagious;
    private Address address;
    private java.util.List<String> symptoms;
    
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
    
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    
    public java.util.List<String> getSymptoms() { return symptoms; }
    public void setSymptoms(java.util.List<String> symptoms) { this.symptoms = symptoms; }
    
    // Inner class for Address
    public static class Address {
        private String zip;
        private String city;
        private String state;
        
        public String getZip() { return zip; }
        public void setZip(String zip) { this.zip = zip; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
    }
}
