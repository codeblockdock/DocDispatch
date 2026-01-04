package online.anshu.docdispatch.dto;

import java.util.Map;

public class HospitalStatsDto {
    private String hospitalId;
    private String hospitalName;
    private String state;
    private String city;
    private boolean active;
    private long stateTotalCases;
    private long hospitalAttendedCases;
    
    // Priority wise for hospital attended
    private Map<String, Long> hospitalPriorityStats;
    
    // Region wise metrics
    private long regionTotalCases;
    private long regionAttendedCases;
    private Map<String, Long> regionPriorityStats;

    public HospitalStatsDto() {}

    // Getters and Setters
    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public long getStateTotalCases() { return stateTotalCases; }
    public void setStateTotalCases(long stateTotalCases) { this.stateTotalCases = stateTotalCases; }

    public long getHospitalAttendedCases() { return hospitalAttendedCases; }
    public void setHospitalAttendedCases(long hospitalAttendedCases) { this.hospitalAttendedCases = hospitalAttendedCases; }

    public Map<String, Long> getHospitalPriorityStats() { return hospitalPriorityStats; }
    public void setHospitalPriorityStats(Map<String, Long> hospitalPriorityStats) { this.hospitalPriorityStats = hospitalPriorityStats; }

    public long getRegionTotalCases() { return regionTotalCases; }
    public void setRegionTotalCases(long regionTotalCases) { this.regionTotalCases = regionTotalCases; }

    public long getRegionAttendedCases() { return regionAttendedCases; }
    public void setRegionAttendedCases(long regionAttendedCases) { this.regionAttendedCases = regionAttendedCases; }

    public Map<String, Long> getRegionPriorityStats() { return regionPriorityStats; }
    public void setRegionPriorityStats(Map<String, Long> regionPriorityStats) { this.regionPriorityStats = regionPriorityStats; }
}
