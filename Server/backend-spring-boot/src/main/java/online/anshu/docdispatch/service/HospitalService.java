package online.anshu.docdispatch.service;

import online.anshu.docdispatch.dto.*;
import online.anshu.docdispatch.entity.Hospital;
import online.anshu.docdispatch.entity.PatientLocation;
import online.anshu.docdispatch.entity.PredictedDisease;
import online.anshu.docdispatch.entity.Query;
import online.anshu.docdispatch.repository.HospitalRepository;
import online.anshu.docdispatch.repository.PatientLocationRepository;
import online.anshu.docdispatch.repository.PredictedDiseaseRepository;
import online.anshu.docdispatch.repository.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
public class HospitalService {
    
    private final HospitalRepository hospitalRepository;
    private final PatientLocationRepository patientLocationRepository;
    private final QueryRepository queryRepository;
    private final PredictedDiseaseRepository predictedDiseaseRepository;
    
    @Autowired
    public HospitalService(HospitalRepository hospitalRepository,
                          PatientLocationRepository patientLocationRepository,
                          QueryRepository queryRepository,
                          PredictedDiseaseRepository predictedDiseaseRepository) {
        this.hospitalRepository = hospitalRepository;
        this.patientLocationRepository = patientLocationRepository;
        this.queryRepository = queryRepository;
        this.predictedDiseaseRepository = predictedDiseaseRepository;
    }
    
    public HospitalLoginResponse login(HospitalLoginRequest request) {
        System.out.println("\n========== HOSPITAL LOGIN ATTEMPT ==========");
        System.out.println("Hospital ID: " + request.getHospitalId());
        
        Optional<Hospital> hospitalOpt = hospitalRepository.findByHospitalIdAndPassword(
            request.getHospitalId(), request.getPassword());
        
        if (hospitalOpt.isEmpty()) {
            System.out.println("Login failed: Invalid credentials");
            return new HospitalLoginResponse(false, "Invalid Hospital ID or Password");
        }
        
        Hospital hospital = hospitalOpt.get();
        
        if (!hospital.isActive()) {
            System.out.println("Login failed: Hospital account is inactive");
            return new HospitalLoginResponse(false, "Hospital account is inactive");
        }
        
        // Update last login
        hospital.setLastLogin(Date.from(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toInstant()));
        hospitalRepository.save(hospital);
        
        // Generate simple token (in production, use JWT)
        String token = Base64.getEncoder().encodeToString(
            (hospital.getHospitalId() + ":" + hospital.getState() + ":" + System.currentTimeMillis()).getBytes()
        );
        
        HospitalLoginResponse response = new HospitalLoginResponse();
        response.setSuccess(true);
        response.setMessage("Login successful");
        response.setToken(token);
        response.setHospitalId(hospital.getHospitalId());
        response.setName(hospital.getName());
        response.setState(hospital.getState());
        response.setCity(hospital.getCity());
        
        System.out.println("Login successful for: " + hospital.getName() + " (State: " + hospital.getState() + ")");
        System.out.println("============================================\n");
        
        return response;
    }
    
    public HospitalLoginResponse register(HospitalRegisterRequest request) {
        System.out.println("\n========== HOSPITAL REGISTRATION ==========");
        System.out.println("Hospital ID: " + request.getHospitalId());
        System.out.println("Name: " + request.getName());
        System.out.println("State: " + request.getState());
        
        if (hospitalRepository.existsByHospitalId(request.getHospitalId())) {
            System.out.println("Registration failed: Hospital ID already exists");
            return new HospitalLoginResponse(false, "Hospital ID already exists");
        }
        
        Hospital hospital = new Hospital();
        hospital.setHospitalId(request.getHospitalId());
        hospital.setName(request.getName());
        hospital.setPassword(request.getPassword());
        hospital.setState(request.getState());
        hospital.setCity(request.getCity());
        hospital.setAddress(request.getAddress());
        hospital.setPhone(request.getPhone());
        hospital.setEmail(request.getEmail());
        hospital.setActive(true);
        
        hospitalRepository.save(hospital);
        
        System.out.println("Hospital registered successfully");
        System.out.println("==========================================\n");
        
        HospitalLoginResponse response = new HospitalLoginResponse();
        response.setSuccess(true);
        response.setMessage("Hospital registered successfully");
        response.setHospitalId(hospital.getHospitalId());
        response.setName(hospital.getName());
        response.setState(hospital.getState());
        
        return response;
    }
    
    public String getHospitalStateFromToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            String[] parts = decoded.split(":");
            if (parts.length >= 2) {
                return parts[1]; // State is the second part
            }
        } catch (Exception e) {
            System.out.println("Error decoding token: " + e.getMessage());
        }
        return null;
    }
    
    public String getHospitalIdFromToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            String[] parts = decoded.split(":");
            if (parts.length >= 1) {
                return parts[0]; // Hospital ID is the first part
            }
        } catch (Exception e) {
            System.out.println("Error decoding token: " + e.getMessage());
        }
        return null;
    }
    
    public List<PatientDashboardDto> getPatientsByState(String state, String search, String disease, String city, String pincode) {
        System.out.println("\n========== FETCHING PATIENTS BY STATE ==========");
        System.out.println("State: " + state);
        System.out.println("Search: " + search + ", Disease: " + disease + ", City: " + city + ", Pincode: " + pincode);
        
        // Get all patient locations in this state
        List<PatientLocation> locations = patientLocationRepository.findByState(state);
        System.out.println("Found " + locations.size() + " patient locations in " + state);
        
        List<PatientDashboardDto> patients = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        
        for (PatientLocation location : locations) {
            // Apply city filter
            if (city != null && !city.isEmpty() && !location.getCity().toLowerCase().contains(city.toLowerCase())) {
                continue;
            }
            
            // Apply pincode filter
            if (pincode != null && !pincode.isEmpty() && !location.getPincode().contains(pincode)) {
                continue;
            }
            
            // Get the query for this location
            Optional<Query> queryOpt = queryRepository.findById(location.getQueryId());
            if (queryOpt.isEmpty()) continue;
            
            Query query = queryOpt.get();
            
            // Apply name search filter
            if (search != null && !search.isEmpty() && 
                !query.getName().toLowerCase().contains(search.toLowerCase())) {
                continue;
            }
            
            // Get predicted disease if available
            Optional<PredictedDisease> predictionOpt = predictedDiseaseRepository.findByQueryId(query.getId());
            String predictedDisease = "";
            int probability = 0;
            
            if (predictionOpt.isPresent()) {
                predictedDisease = predictionOpt.get().getDisease();
                probability = (int) (predictionOpt.get().getProbability() * 100);
            }
            
            // Apply disease filter
            if (disease != null && !disease.isEmpty() && 
                !predictedDisease.toLowerCase().contains(disease.toLowerCase())) {
                continue;
            }
            
            PatientDashboardDto dto = new PatientDashboardDto();
            dto.setId(query.getId());
            dto.setName(query.getName());
            dto.setSymptoms(query.getSymptoms());
            dto.setPredictedDisease(predictedDisease);
            dto.setProbability(probability);
            dto.setCity(location.getCity());
            dto.setState(location.getState());
            dto.setPincode(location.getPincode());
            dto.setContact(query.getContact());
            dto.setAge(query.getAge());
            dto.setGender(query.getGender());
            dto.setTemperature(query.getTemperature());
            dto.setDays(query.getDays());
            dto.setContagious(query.getContagious());
            dto.setAttended(query.getAttended());
            
            // Set status based on attended flag and probability
            if (query.getAttended() == 1) {
                dto.setStatus("Attended");
            } else if (probability >= 70) {
                dto.setStatus("High Risk");
            } else if (probability >= 40) {
                dto.setStatus("Medium Risk");
            } else {
                dto.setStatus("Pending");
            }
            
            if (query.getReceivedAt() != null) {
                dto.setReceivedAt(formatter.format(query.getReceivedAt()));
            }
            
            patients.add(dto);
        }
        
        // Sort by receivedAt descending (newest first)
        patients.sort((a, b) -> {
            if (a.getReceivedAt() == null) return 1;
            if (b.getReceivedAt() == null) return -1;
            return b.getReceivedAt().compareTo(a.getReceivedAt());
        });
        
        System.out.println("Returning " + patients.size() + " patients after filtering");
        System.out.println("================================================\n");
        
        return patients;
    }
    
    public DashboardStatsDto getStatsByState(String state) {
        System.out.println("\n========== FETCHING STATS BY STATE ==========");
        System.out.println("State: " + state);
        
        List<PatientLocation> locations = patientLocationRepository.findByState(state);
        
        DashboardStatsDto stats = new DashboardStatsDto();
        long totalPatients = 0;
        long highRiskCases = 0;
        long newlyReported = 0;
        long emergencyPriority = 0;
        long attendedCases = 0;
        long pendingCases = 0;
        
        Date twentyFourHoursAgo = Date.from(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).minus(24, ChronoUnit.HOURS).toInstant());
        
        for (PatientLocation location : locations) {
            Optional<Query> queryOpt = queryRepository.findById(location.getQueryId());
            if (queryOpt.isEmpty()) continue;
            
            Query query = queryOpt.get();
            totalPatients++;
            
            if (query.getAttended() == 1) {
                attendedCases++;
            } else {
                pendingCases++;
            }
            
            // Check if newly reported (within last 24 hours)
            if (query.getReceivedAt() != null && query.getReceivedAt().after(twentyFourHoursAgo)) {
                newlyReported++;
            }
            
            // Check prediction for high risk and emergency
            Optional<PredictedDisease> predictionOpt = predictedDiseaseRepository.findByQueryId(query.getId());
            if (predictionOpt.isPresent()) {
                double probability = predictionOpt.get().getProbability();
                if (probability >= 0.7) {
                    highRiskCases++;
                }
                if (probability >= 0.85 || query.getTemperature() >= 103) {
                    emergencyPriority++;
                }
            }
        }
        
        stats.setTotalPatients(totalPatients);
        stats.setHighRiskCases(highRiskCases);
        stats.setNewlyReported(newlyReported);
        stats.setEmergencyPriority(emergencyPriority);
        stats.setAttendedCases(attendedCases);
        stats.setPendingCases(pendingCases);
        
        System.out.println("Stats - Total: " + totalPatients + ", High Risk: " + highRiskCases + 
                          ", New: " + newlyReported + ", Emergency: " + emergencyPriority);
        System.out.println("===========================================\n");
        
        return stats;
    }
    
    public PatientDashboardDto getPatientById(String id, String state) {
        Optional<Query> queryOpt = queryRepository.findById(id);
        if (queryOpt.isEmpty()) return null;
        
        Query query = queryOpt.get();
        
        // Verify the patient belongs to the hospital's state
        Optional<PatientLocation> locationOpt = patientLocationRepository.findByQueryId(id);
        if (locationOpt.isEmpty()) return null;
        
        PatientLocation location = locationOpt.get();
        if (!location.getState().equalsIgnoreCase(state)) {
            System.out.println("Access denied: Patient not in hospital's state");
            return null;
        }
        
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        
        PatientDashboardDto dto = new PatientDashboardDto();
        dto.setId(query.getId());
        dto.setName(query.getName());
        dto.setSymptoms(query.getSymptoms());
        dto.setCity(location.getCity());
        dto.setState(location.getState());
        dto.setPincode(location.getPincode());
        dto.setContact(query.getContact());
        dto.setAge(query.getAge());
        dto.setGender(query.getGender());
        dto.setTemperature(query.getTemperature());
        dto.setDays(query.getDays());
        dto.setContagious(query.getContagious());
        dto.setAttended(query.getAttended());
        
        Optional<PredictedDisease> predictionOpt = predictedDiseaseRepository.findByQueryId(query.getId());
        if (predictionOpt.isPresent()) {
            dto.setPredictedDisease(predictionOpt.get().getDisease());
            dto.setProbability((int) (predictionOpt.get().getProbability() * 100));
        }
        
        if (query.getAttended() == 1) {
            dto.setStatus("Attended");
        } else if (dto.getProbability() >= 70) {
            dto.setStatus("High Risk");
        } else if (dto.getProbability() >= 40) {
            dto.setStatus("Medium Risk");
        } else {
            dto.setStatus("Pending");
        }
        
        if (query.getReceivedAt() != null) {
            dto.setReceivedAt(formatter.format(query.getReceivedAt()));
        }
        
        return dto;
    }
    
    public boolean deletePatient(String id, String state) {
        // Verify the patient belongs to the hospital's state
        Optional<PatientLocation> locationOpt = patientLocationRepository.findByQueryId(id);
        if (locationOpt.isEmpty()) return false;
        
        PatientLocation location = locationOpt.get();
        if (!location.getState().equalsIgnoreCase(state)) {
            System.out.println("Access denied: Cannot delete patient from another state");
            return false;
        }
        
        // Delete related records
        patientLocationRepository.deleteById(id);
        predictedDiseaseRepository.deleteById(id);
        queryRepository.deleteById(id);
        
        System.out.println("Patient " + id + " deleted successfully");
        return true;
    }
}
