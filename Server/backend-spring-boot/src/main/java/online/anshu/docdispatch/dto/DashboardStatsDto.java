package online.anshu.docdispatch.dto;

public class DashboardStatsDto {
    private long totalPatients;
    private long highRiskCases;
    private long newlyReported;
    private long emergencyPriority;
    private long attendedCases;
    private long pendingCases;
    
    // Hospital stats
    private long totalHospitals;
    private long activeHospitals;
    private long statesCovered;
    private long citiesCovered;
    
    // Getters and Setters
    public long getTotalPatients() { return totalPatients; }
    public void setTotalPatients(long totalPatients) { this.totalPatients = totalPatients; }
    
    public long getHighRiskCases() { return highRiskCases; }
    public void setHighRiskCases(long highRiskCases) { this.highRiskCases = highRiskCases; }
    
    public long getNewlyReported() { return newlyReported; }
    public void setNewlyReported(long newlyReported) { this.newlyReported = newlyReported; }
    
    public long getEmergencyPriority() { return emergencyPriority; }
    public void setEmergencyPriority(long emergencyPriority) { this.emergencyPriority = emergencyPriority; }
    
    public long getAttendedCases() { return attendedCases; }
    public void setAttendedCases(long attendedCases) { this.attendedCases = attendedCases; }
    
    public long getPendingCases() { return pendingCases; }
    public void setPendingCases(long pendingCases) { this.pendingCases = pendingCases; }
    
    public long getTotalHospitals() { return totalHospitals; }
    public void setTotalHospitals(long totalHospitals) { this.totalHospitals = totalHospitals; }
    
    public long getActiveHospitals() { return activeHospitals; }
    public void setActiveHospitals(long activeHospitals) { this.activeHospitals = activeHospitals; }
    
    public long getStatesCovered() { return statesCovered; }
    public void setStatesCovered(long statesCovered) { this.statesCovered = statesCovered; }
    
    public long getCitiesCovered() { return citiesCovered; }
    public void setCitiesCovered(long citiesCovered) { this.citiesCovered = citiesCovered; }
}
