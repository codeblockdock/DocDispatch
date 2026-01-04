package online.anshu.docdispatch.service;

import online.anshu.docdispatch.dto.*;
import online.anshu.docdispatch.entity.Attended;
import online.anshu.docdispatch.entity.Hospital;
import online.anshu.docdispatch.entity.PatientLocation;
import online.anshu.docdispatch.entity.PredictedDisease;
import online.anshu.docdispatch.entity.Query;
import online.anshu.docdispatch.repository.AttendedRepository;
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
    private final AttendedRepository attendedRepository;
    
    @Autowired
    public HospitalService(HospitalRepository hospitalRepository,
                          PatientLocationRepository patientLocationRepository,
                          QueryRepository queryRepository,
                          PredictedDiseaseRepository predictedDiseaseRepository,
                          AttendedRepository attendedRepository) {
        this.hospitalRepository = hospitalRepository;
        this.patientLocationRepository = patientLocationRepository;
        this.queryRepository = queryRepository;
        this.predictedDiseaseRepository = predictedDiseaseRepository;
        this.attendedRepository = attendedRepository;
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
        
        // Temporary fix: If hospital ID is "admin" or "adminAtDD", force admin role
        if ("admin".equalsIgnoreCase(hospital.getHospitalId()) || "adminAtDD".equalsIgnoreCase(hospital.getHospitalId())) {
            hospital.setAdmin(true);
            hospitalRepository.save(hospital);
        }
        
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
        response.setAdmin(hospital.isAdmin());
        
        System.out.println("Login successful for: " + hospital.getName() + " (State: " + hospital.getState() + ")");
        System.out.println("============================================\n");
        
        return response;
    }
    
    public HospitalLoginResponse register(HospitalRegisterRequest request) {
        System.out.println("\n========== HOSPITAL REGISTRATION REQUEST ==========");
        System.out.println("Hospital ID: " + request.getHospitalId());
        System.out.println("Name: " + request.getName());
        System.out.println("Note: Registration is disabled. Create hospitals manually in MongoDB.");
        System.out.println("=====================================================\n");
        
        // This endpoint is deprecated. Hospital creation should be done manually in MongoDB.
        // For partner registration requests, use EmailJS from React frontend.
        // This is here only for backwards compatibility.
        
        return new HospitalLoginResponse(false, "Hospital registration endpoint is disabled. Please contact admin to create hospital manually.");
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
    
    public List<PatientDashboardDto> getAllPatients(String search, String disease, String city, String pincode, String riskFactor) {
        System.out.println("\n========== FETCHING ALL PATIENTS (ADMIN) ==========");
        System.out.println("Search: " + search + ", Disease: " + disease + ", City: " + city + ", Pincode: " + pincode + ", RiskFactor: " + riskFactor);
        
        // Get all patient locations
        List<PatientLocation> locations = patientLocationRepository.findAll();
        System.out.println("Found " + locations.size() + " total patient locations");
        
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

            // Apply risk factor filter
            if (riskFactor != null && !riskFactor.isEmpty()) {
                boolean matches = false;
                if (riskFactor.equalsIgnoreCase("low") && query.getRiskfactor() == 1.0) matches = true;
                else if (riskFactor.equalsIgnoreCase("medium") && query.getRiskfactor() == 1.5) matches = true;
                else if (riskFactor.equalsIgnoreCase("high") && query.getRiskfactor() == 3.0) matches = true;
                else if (riskFactor.equalsIgnoreCase("priority")) {
                    if ((query.getAge() >= 5 && query.getAge() <= 12) || (query.getAge() >= 51 && query.getAge() <= 60)) {
                        matches = true;
                    }
                }
                
                if (!matches) continue;
            }
            
            PatientDashboardDto dto = new PatientDashboardDto();
            dto.setId(query.getId());
            dto.setName(query.getName());
            dto.setSymptoms(query.getSymptoms());
            dto.setPredictedDisease(predictedDisease);
            dto.setProbability(probability);
            dto.setCity(location.getCity());
            dto.setVillage(location.getVillage());
            dto.setState(location.getState());
            dto.setPincode(location.getPincode());
            dto.setContact(query.getContact());
            dto.setAge(query.getAge());
            dto.setGender(query.getGender());
            dto.setTemperature(query.getTemperature());
            dto.setDays(query.getDays());
            dto.setRiskfactor(query.getRiskfactor());
            dto.setAttended(query.getAttended());
            
            // Set status based on attended flag and riskfactor/probability
            if (query.getAttended() == 1) {
                dto.setStatus("Attended");
            } else if (query.getRiskfactor() == 3.0 || probability >= 70) {
                dto.setStatus("High Risk");
            } else if (query.getRiskfactor() == 1.5 || probability >= 40) {
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

    public List<PatientDashboardDto> getPatientsByState(String state, String search, String disease, String city, String pincode, String riskFactor) {
        System.out.println("\n========== FETCHING PATIENTS BY STATE ==========");
        System.out.println("State: " + state);
        System.out.println("Search: " + search + ", Disease: " + disease + ", City: " + city + ", Pincode: " + pincode + ", RiskFactor: " + riskFactor);
        
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

            // Apply risk factor filter
            if (riskFactor != null && !riskFactor.isEmpty()) {
                boolean matches = false;
                if (riskFactor.equalsIgnoreCase("low") && query.getRiskfactor() == 1.0) matches = true;
                else if (riskFactor.equalsIgnoreCase("medium") && query.getRiskfactor() == 1.5) matches = true;
                else if (riskFactor.equalsIgnoreCase("high") && query.getRiskfactor() == 3.0) matches = true;
                else if (riskFactor.equalsIgnoreCase("priority")) {
                    if ((query.getAge() >= 5 && query.getAge() <= 12) || (query.getAge() >= 51 && query.getAge() <= 60)) {
                        matches = true;
                    }
                }
                
                if (!matches) continue;
            }
            
            PatientDashboardDto dto = new PatientDashboardDto();
            dto.setId(query.getId());
            dto.setName(query.getName());
            dto.setSymptoms(query.getSymptoms());
            dto.setPredictedDisease(predictedDisease);
            dto.setProbability(probability);
            dto.setCity(location.getCity());
            dto.setVillage(location.getVillage());
            dto.setState(location.getState());
            dto.setPincode(location.getPincode());
            dto.setContact(query.getContact());
            dto.setAge(query.getAge());
            dto.setGender(query.getGender());
            dto.setTemperature(query.getTemperature());
            dto.setDays(query.getDays());
            dto.setRiskfactor(query.getRiskfactor());
            dto.setAttended(query.getAttended());
            
            // Set status based on attended flag and riskfactor/probability
            if (query.getAttended() == 1) {
                dto.setStatus("Attended");
            } else if (query.getRiskfactor() == 3.0 || probability >= 70) {
                dto.setStatus("High Risk");
            } else if (query.getRiskfactor() == 1.5 || probability >= 40) {
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

    public List<PatientDashboardDto> getPatientsByRegion(List<String> regionPincodes, String search, String disease, String city, String pincode, String riskFactor) {
        System.out.println("\n========== FETCHING PATIENTS BY REGION ==========");
        System.out.println("Region Pincodes: " + regionPincodes);
        System.out.println("Search: " + search + ", Disease: " + disease + ", City: " + city + ", Pincode: " + pincode + ", RiskFactor: " + riskFactor);
        
        if (regionPincodes == null || regionPincodes.isEmpty()) {
            System.out.println("No region pincodes assigned, returning empty list");
            return new ArrayList<>();
        }

        // Clean up region pincodes
        List<String> cleanedPincodes = cleanRegionPincodes(regionPincodes);
        System.out.println("Cleaned Pincodes: " + cleanedPincodes);

        // Get all patient locations
        List<PatientLocation> allLocations = patientLocationRepository.findAll();
        
        // Filter by region pincodes
        List<PatientLocation> locations = new ArrayList<>();
        for (PatientLocation loc : allLocations) {
            if (loc.getPincode() != null && cleanedPincodes.contains(loc.getPincode().trim())) {
                locations.add(loc);
            }
        }
        System.out.println("Found " + locations.size() + " patient locations in assigned regions");
        
        List<PatientDashboardDto> patients = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        
        for (PatientLocation location : locations) {
            // Apply city filter
            if (city != null && !city.isEmpty() && !location.getCity().toLowerCase().contains(city.toLowerCase())) {
                continue;
            }
            
            // Apply pincode filter (additional filter on top of region)
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

            // Apply risk factor filter
            if (riskFactor != null && !riskFactor.isEmpty()) {
                boolean matches = false;
                if (riskFactor.equalsIgnoreCase("low") && query.getRiskfactor() == 1.0) matches = true;
                else if (riskFactor.equalsIgnoreCase("medium") && query.getRiskfactor() == 1.5) matches = true;
                else if (riskFactor.equalsIgnoreCase("high") && query.getRiskfactor() == 3.0) matches = true;
                else if (riskFactor.equalsIgnoreCase("priority")) {
                    if ((query.getAge() >= 5 && query.getAge() <= 12) || (query.getAge() >= 51 && query.getAge() <= 60)) {
                        matches = true;
                    }
                }
                
                if (!matches) continue;
            }
            
            PatientDashboardDto dto = new PatientDashboardDto();
            dto.setId(query.getId());
            dto.setName(query.getName());
            dto.setSymptoms(query.getSymptoms());
            dto.setPredictedDisease(predictedDisease);
            dto.setProbability(probability);
            dto.setCity(location.getCity());
            dto.setVillage(location.getVillage());
            dto.setState(location.getState());
            dto.setPincode(location.getPincode());
            dto.setContact(query.getContact());
            dto.setAge(query.getAge());
            dto.setGender(query.getGender());
            dto.setTemperature(query.getTemperature());
            dto.setDays(query.getDays());
            dto.setRiskfactor(query.getRiskfactor());
            dto.setAttended(query.getAttended());
            
            // Set status based on attended flag and riskfactor/probability
            if (query.getAttended() == 1) {
                dto.setStatus("Attended");
            } else if (query.getRiskfactor() == 3.0 || probability >= 70) {
                dto.setStatus("High Risk");
            } else if (query.getRiskfactor() == 1.5 || probability >= 40) {
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
        
        return calculateStats(locations, "State: " + state);
    }
    
    public DashboardStatsDto getAllStats() {
        System.out.println("\n========== FETCHING ALL STATS (ADMIN) ==========");
        
        List<PatientLocation> locations = patientLocationRepository.findAll();
        
        return calculateStats(locations, "All States (Admin)");
    }

    public DashboardStatsDto getStatsByRegion(List<String> regionPincodes) {
        System.out.println("\n========== FETCHING STATS BY REGION ==========");
        System.out.println("Region Pincodes: " + regionPincodes);
        
        if (regionPincodes == null || regionPincodes.isEmpty()) {
            System.out.println("No region pincodes assigned, returning empty stats");
            return new DashboardStatsDto();
        }

        // Clean up region pincodes
        List<String> cleanedPincodes = cleanRegionPincodes(regionPincodes);
        System.out.println("Cleaned Pincodes: " + cleanedPincodes);

        // Get all patient locations
        List<PatientLocation> allLocations = patientLocationRepository.findAll();
        
        // Filter by region pincodes
        List<PatientLocation> locations = new ArrayList<>();
        for (PatientLocation loc : allLocations) {
            if (loc.getPincode() != null && cleanedPincodes.contains(loc.getPincode().trim())) {
                locations.add(loc);
            }
        }
        System.out.println("Found " + locations.size() + " patient locations in assigned regions");
        
        return calculateStats(locations, "Region: " + cleanedPincodes);
    }
    
    private DashboardStatsDto calculateStats(List<PatientLocation> locations, String context) {
        DashboardStatsDto stats = new DashboardStatsDto();
        long totalPatients = 0;
        long highRiskCases = 0;
        long newlyReported = 0;
        long emergencyPriority = 0;
        long attendedCases = 0;
        long pendingCases = 0;
        
        // Track unique states and cities from patient locations
        Set<String> uniqueStates = new HashSet<>();
        Set<String> uniqueCities = new HashSet<>();
        
        Date twentyFourHoursAgo = Date.from(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).minus(24, ChronoUnit.HOURS).toInstant());
        
        for (PatientLocation location : locations) {
            Optional<Query> queryOpt = queryRepository.findById(location.getQueryId());
            if (queryOpt.isEmpty()) continue;
            
            Query query = queryOpt.get();
            totalPatients++;
            
            // Track unique states and cities
            if (location.getState() != null && !location.getState().isEmpty()) {
                uniqueStates.add(location.getState().toLowerCase());
            }
            if (location.getCity() != null && !location.getCity().isEmpty()) {
                uniqueCities.add(location.getCity().toLowerCase());
            }
            
            if (query.getAttended() == 1) {
                attendedCases++;
            } else {
                pendingCases++;
            }
            
            // Check if newly reported (within last 24 hours)
            if (query.getReceivedAt() != null && query.getReceivedAt().after(twentyFourHoursAgo)) {
                newlyReported++;
            }
            
            // High Risk Cases: riskfactor is 3.0
            if (query.getRiskfactor() == 3.0) {
                highRiskCases++;
                // If attended, minus 1
                if (query.getAttended() == 1) {
                    highRiskCases--;
                }
            }

            // Priority Cases: age in 5-12 or 51-60 inclusive
            if ((query.getAge() >= 5 && query.getAge() <= 12) || (query.getAge() >= 51 && query.getAge() <= 60)) {
                emergencyPriority++;
                // If attended, minus 1
                if (query.getAttended() == 1) {
                    emergencyPriority--;
                }
            }
        }
        
        // Calculate hospital stats
        List<Hospital> allHospitals = hospitalRepository.findAll();
        long totalHospitals = allHospitals.stream().filter(h -> !h.isAdmin()).count();
        long activeHospitals = allHospitals.stream().filter(h -> !h.isAdmin() && h.isActive()).count();
        
        stats.setTotalPatients(totalPatients);
        stats.setHighRiskCases(highRiskCases);
        stats.setNewlyReported(newlyReported);
        stats.setEmergencyPriority(emergencyPriority);
        stats.setAttendedCases(attendedCases);
        stats.setPendingCases(pendingCases);
        stats.setTotalHospitals(totalHospitals);
        stats.setActiveHospitals(activeHospitals);
        stats.setStatesCovered(uniqueStates.size());
        stats.setCitiesCovered(uniqueCities.size());
        
        System.out.println("Stats for " + context + " - Total: " + totalPatients + ", High Risk: " + highRiskCases + 
                          ", New: " + newlyReported + ", Emergency: " + emergencyPriority +
                          ", Hospitals: " + totalHospitals + ", Active: " + activeHospitals +
                          ", States: " + uniqueStates.size() + ", Cities: " + uniqueCities.size());
        System.out.println("===========================================");
        
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
        dto.setVillage(location.getVillage());
        dto.setState(location.getState());
        dto.setPincode(location.getPincode());
        dto.setContact(query.getContact());
        dto.setAge(query.getAge());
        dto.setGender(query.getGender());
        dto.setTemperature(query.getTemperature());
        dto.setDays(query.getDays());
        dto.setRiskfactor(query.getRiskfactor());
        dto.setAttended(query.getAttended());
        
        Optional<PredictedDisease> predictionOpt = predictedDiseaseRepository.findByQueryId(query.getId());
        if (predictionOpt.isPresent()) {
            dto.setPredictedDisease(predictionOpt.get().getDisease());
            dto.setProbability((int) (predictionOpt.get().getProbability() * 100));
        }
        
        // Fetch attended details if patient has been attended
        if (query.getAttended() == 1) {
            dto.setStatus("Attended");
            attendedRepository.findByQueryId(query.getId()).ifPresent(attended -> {
                dto.setDoctor(attended.getDoctor());
                dto.setHospital(attended.getHospital());
                dto.setCity(attended.getCity());
                dto.setDiagnosis(attended.getDiagnosis());
                dto.setTreatment(attended.getTreatment());
                dto.setAdvice(attended.getAdvice());
                dto.setAppointment(attended.getAppointment());
                // Set attended timestamp from attended collection
                if (attended.getTimestamp() != null) {
                    dto.setAttendedTimestamp(formatter.format(attended.getTimestamp()));
                }
            });
        } else if (query.getRiskfactor() == 3.0 || dto.getProbability() >= 70) {
            dto.setStatus("High Risk");
        } else if (query.getRiskfactor() == 1.5 || dto.getProbability() >= 40) {
            dto.setStatus("Medium Risk");
        } else {
            dto.setStatus("Pending");
        }
        
        if (query.getReceivedAt() != null) {
            dto.setReceivedAt(formatter.format(query.getReceivedAt()));
        }
        
        return dto;
    }

    /**
     * Helper method to clean region pincodes from various formats.
     * Handles cases like:
     * - ["[462001", "462002", "462005]"] (brackets as part of strings)
     * - "[462001, 462002, 462003]" (single string with all pincodes)
     * - ["462001", "462002"] (clean array)
     */
    private List<String> cleanRegionPincodes(List<String> regionPincodes) {
        List<String> cleanedPincodes = new ArrayList<>();
        
        for (String p : regionPincodes) {
            if (p == null) continue;
            
            // Remove brackets and split by comma if it contains multiple pincodes
            String cleaned = p.replaceAll("[\\[\\]]", "").trim();
            
            // If it contains comma, it might be a comma-separated list
            if (cleaned.contains(",")) {
                String[] parts = cleaned.split(",");
                for (String part : parts) {
                    String trimmed = part.trim();
                    if (!trimmed.isEmpty()) {
                        cleanedPincodes.add(trimmed);
                    }
                }
            } else if (!cleaned.isEmpty()) {
                cleanedPincodes.add(cleaned);
            }
        }
        
        return cleanedPincodes;
    }
    
    public PatientDashboardDto getPatientByIdForAdmin(String id) {
        Optional<Query> queryOpt = queryRepository.findById(id);
        if (queryOpt.isEmpty()) return null;
        
        Query query = queryOpt.get();
        
        // Get patient location (no state restriction for admin)
        Optional<PatientLocation> locationOpt = patientLocationRepository.findByQueryId(id);
        if (locationOpt.isEmpty()) return null;
        
        PatientLocation location = locationOpt.get();
        
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        
        PatientDashboardDto dto = new PatientDashboardDto();
        dto.setId(query.getId());
        dto.setName(query.getName());
        dto.setSymptoms(query.getSymptoms());
        dto.setCity(location.getCity());
        dto.setVillage(location.getVillage());
        dto.setState(location.getState());
        dto.setPincode(location.getPincode());
        dto.setContact(query.getContact());
        dto.setAge(query.getAge());
        dto.setGender(query.getGender());
        dto.setTemperature(query.getTemperature());
        dto.setDays(query.getDays());
        dto.setRiskfactor(query.getRiskfactor());
        dto.setAttended(query.getAttended());
        
        Optional<PredictedDisease> predictionOpt = predictedDiseaseRepository.findByQueryId(query.getId());
        if (predictionOpt.isPresent()) {
            dto.setPredictedDisease(predictionOpt.get().getDisease());
            dto.setProbability((int) (predictionOpt.get().getProbability() * 100));
        }
        
        // Fetch attended details if patient has been attended
        if (query.getAttended() == 1) {
            dto.setStatus("Attended");
            attendedRepository.findByQueryId(query.getId()).ifPresent(attended -> {
                dto.setDoctor(attended.getDoctor());
                dto.setHospital(attended.getHospital());
                dto.setCity(attended.getCity());
                dto.setDiagnosis(attended.getDiagnosis());
                dto.setTreatment(attended.getTreatment());
                dto.setAdvice(attended.getAdvice());
                dto.setAppointment(attended.getAppointment());
                // Set attended timestamp from attended collection
                if (attended.getTimestamp() != null) {
                    dto.setAttendedTimestamp(formatter.format(attended.getTimestamp()));
                }
            });
        } else if (query.getRiskfactor() == 3.0 || dto.getProbability() >= 70) {
            dto.setStatus("High Risk");
        } else if (query.getRiskfactor() == 1.5 || dto.getProbability() >= 40) {
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
        attendedRepository.deleteById(id);
        queryRepository.deleteById(id);
        
        System.out.println("Patient " + id + " deleted successfully");
        return true;
    }
    
    public boolean deletePatientForAdmin(String id) {
        // Admin can delete any patient (no state check)
        Optional<PatientLocation> locationOpt = patientLocationRepository.findByQueryId(id);
        if (locationOpt.isEmpty()) return false;
        
        // Delete related records
        patientLocationRepository.deleteById(id);
        predictedDiseaseRepository.deleteById(id);
        attendedRepository.deleteById(id);
        queryRepository.deleteById(id);
        
        System.out.println("Patient " + id + " deleted successfully by admin");
        return true;
    }
    
    public Optional<Hospital> findByHospitalId(String hospitalId) {
        return hospitalRepository.findByHospitalId(hospitalId);
    }
    
    public void saveHospital(Hospital hospital) {
        hospitalRepository.save(hospital);
    }

    public List<HospitalStatsDto> getHospitalStatsList() {
        System.out.println("\n========== FETCHING ALL HOSPITAL STATS (ADMIN) ==========");
        
        List<Hospital> hospitals = hospitalRepository.findAll();
        List<Query> allQueries = queryRepository.findAll();
        List<PatientLocation> allLocations = patientLocationRepository.findAll();
        List<Attended> allAttended = attendedRepository.findAll();
        
        // Map queryId to location for quick lookup
        Map<String, PatientLocation> locationMap = new HashMap<>();
        for (PatientLocation loc : allLocations) {
            locationMap.put(loc.getQueryId(), loc);
        }
        
        // Map queryId to query for quick lookup
        Map<String, Query> queryMap = new HashMap<>();
        for (Query q : allQueries) {
            queryMap.put(q.getId(), q);
        }

        List<HospitalStatsDto> result = new ArrayList<>();
        
        for (Hospital hospital : hospitals) {
            if (hospital.isAdmin()) continue;
            
            HospitalStatsDto dto = new HospitalStatsDto();
            dto.setHospitalId(hospital.getHospitalId());
            dto.setHospitalName(hospital.getName());
            dto.setState(hospital.getState());
            dto.setCity(hospital.getCity());
            dto.setActive(hospital.isActive());
            
            String hospitalState = hospital.getState();
            List<String> hospitalRegion = hospital.getRegion() != null ? hospital.getRegion() : new ArrayList<>();
            
            long stateTotal = 0;
            long hospitalAttended = 0;
            long regionTotal = 0;
            long regionAttended = 0;
            
            Map<String, Long> hospitalPriority = new HashMap<>();
            hospitalPriority.put("Low", 0L);
            hospitalPriority.put("Medium", 0L);
            hospitalPriority.put("High", 0L);
            hospitalPriority.put("Priority", 0L);
            
            Map<String, Long> regionPriority = new HashMap<>();
            regionPriority.put("Low", 0L);
            regionPriority.put("Medium", 0L);
            regionPriority.put("High", 0L);
            regionPriority.put("Priority", 0L);
            
            // Calculate state total
            for (PatientLocation loc : allLocations) {
                if (loc.getState() != null && loc.getState().equalsIgnoreCase(hospitalState)) {
                    stateTotal++;
                }
                
                // Calculate region total
                if (hospitalRegion.contains(loc.getPincode())) {
                    regionTotal++;
                }
            }
            
            // Calculate hospital attended and priority stats
            for (Attended attended : allAttended) {
                if (attended.getHospital() != null && attended.getHospital().equalsIgnoreCase(hospital.getName())) {
                    hospitalAttended++;
                    
                    Query q = queryMap.get(attended.getQueryId());
                    if (q != null) {
                        updatePriorityStats(hospitalPriority, q);
                        
                        // Check if this attended query is in the hospital's region
                        PatientLocation loc = locationMap.get(attended.getQueryId());
                        if (loc != null && hospitalRegion.contains(loc.getPincode())) {
                            regionAttended++;
                            updatePriorityStats(regionPriority, q);
                        }
                    }
                }
            }
            
            dto.setStateTotalCases(stateTotal);
            dto.setHospitalAttendedCases(hospitalAttended);
            dto.setHospitalPriorityStats(hospitalPriority);
            dto.setRegionTotalCases(regionTotal);
            dto.setRegionAttendedCases(regionAttended);
            dto.setRegionPriorityStats(regionPriority);
            
            result.add(dto);
        }
        
        return result;
    }

    public boolean toggleHospitalStatus(String hospitalId) {
        Optional<Hospital> hospitalOpt = hospitalRepository.findByHospitalId(hospitalId);
        if (hospitalOpt.isPresent()) {
            Hospital hospital = hospitalOpt.get();
            hospital.setActive(!hospital.isActive());
            hospitalRepository.save(hospital);
            System.out.println("Hospital " + hospitalId + " status toggled to: " + hospital.isActive());
            return true;
        }
        return false;
    }

    private void updatePriorityStats(Map<String, Long> stats, Query q) {
        if (q.getRiskfactor() == 1.0) stats.put("Low", stats.get("Low") + 1);
        else if (q.getRiskfactor() == 1.5) stats.put("Medium", stats.get("Medium") + 1);
        else if (q.getRiskfactor() == 3.0) stats.put("High", stats.get("High") + 1);
        
        if ((q.getAge() >= 5 && q.getAge() <= 12) || (q.getAge() >= 51 && q.getAge() <= 60)) {
            stats.put("Priority", stats.get("Priority") + 1);
        }
    }
}
