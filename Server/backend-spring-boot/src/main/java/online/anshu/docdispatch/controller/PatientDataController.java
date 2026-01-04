package online.anshu.docdispatch.controller;

import online.anshu.docdispatch.dto.PatientDataRequest;
import online.anshu.docdispatch.entity.PatientLocation;
import online.anshu.docdispatch.entity.PredictedDisease;
import online.anshu.docdispatch.entity.Query;
import online.anshu.docdispatch.repository.PatientLocationRepository;
import online.anshu.docdispatch.repository.PredictedDiseaseRepository;
import online.anshu.docdispatch.repository.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.time.ZonedDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PatientDataController {
    
    private final QueryRepository queryRepository;
    private final PatientLocationRepository patientLocationRepository;
    private final PredictedDiseaseRepository predictedDiseaseRepository;
    
    @Autowired
    public PatientDataController(QueryRepository queryRepository,
                                 PatientLocationRepository patientLocationRepository,
                                 PredictedDiseaseRepository predictedDiseaseRepository) {
        this.queryRepository = queryRepository;
        this.patientLocationRepository = patientLocationRepository;
        this.predictedDiseaseRepository = predictedDiseaseRepository;
    }
    
    /**
     * POST /api/patientData
     * Receives prediction data from external app and stores it
     * 
     * JSON body example:
     * {
     *   "name": "Patient Name",
     *   "symptoms": "...",
     *   "predictedDisease": "...",
     *   "probability": 87,
     *   "location_id": "69555a242b6256a404826359"
     * }
     */
    @PostMapping("/patientData")
    public ResponseEntity<?> receivePatientData(@RequestBody PatientDataRequest request) {
        System.out.println("\n========== RECEIVING PATIENT DATA FROM EXTERNAL APP ==========");
        System.out.println("Name: " + request.getName());
        System.out.println("Symptoms: " + request.getSymptoms());
        System.out.println("Predicted Disease: " + request.getPredictedDisease());
        System.out.println("Probability: " + request.getProbability());
        System.out.println("Location ID: " + request.getLocationId());
        
        try {
            // Verify location exists
            Optional<PatientLocation> locationOpt = patientLocationRepository.findById(request.getLocationId());
            
            PatientLocation location;
            if (locationOpt.isEmpty()) {
                // If location doesn't exist, we need to create or handle it differently
                System.out.println("Warning: Location ID not found, proceeding anyway");
                location = null;
            } else {
                location = locationOpt.get();
                System.out.println("Location found: " + location.getCity() + ", " + location.getState());
            }
            
            // Create Query record
            Query query = new Query();
            query.setName(request.getName());
            query.setSymptoms(request.getSymptoms());
            query.setContact(request.getContact() != null ? request.getContact() : "");
            query.setAge(request.getAge());
            query.setGender(request.getGender() != null ? request.getGender() : "");
            query.setTemperature(request.getTemperature());
            query.setDays(request.getDays());
            query.setRiskfactor(request.getRiskfactor());
            query.setAttended(0);
            query.setReceivedAt(Date.from(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toInstant()));
            
            Query savedQuery = queryRepository.save(query);
            System.out.println("Query saved with ID: " + savedQuery.getId());
            
            // If location_id is provided but location doesn't exist, create new location entry
            if (location == null && request.getLocationId() != null && !request.getLocationId().isEmpty()) {
                // Create a placeholder location linked to this query
                PatientLocation newLocation = new PatientLocation();
                newLocation.setQueryId(savedQuery.getId());
                newLocation.setPincode("");
                newLocation.setCity("Unknown");
                newLocation.setVillage("Unknown");
                newLocation.setState("Unknown");
                patientLocationRepository.save(newLocation);
            } else if (location != null) {
                // Update or create location for this query
                PatientLocation queryLocation = new PatientLocation();
                queryLocation.setQueryId(savedQuery.getId());
                queryLocation.setPincode(location.getPincode());
                queryLocation.setCity(location.getCity());
                queryLocation.setVillage(location.getVillage());
                queryLocation.setState(location.getState());
                patientLocationRepository.save(queryLocation);
            }
            
            // Save predicted disease
            if (request.getPredictedDisease() != null && !request.getPredictedDisease().isEmpty()) {
                PredictedDisease prediction = new PredictedDisease();
                prediction.setQueryId(savedQuery.getId());
                prediction.setDisease(request.getPredictedDisease());
                prediction.setProbability(request.getProbability() / 100.0); // Convert to decimal
                predictedDiseaseRepository.save(prediction);
                System.out.println("Prediction saved: " + request.getPredictedDisease() + " (" + request.getProbability() + "%)");
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Patient data received and stored successfully");
            response.put("queryId", savedQuery.getId());
            
            System.out.println("Patient data stored successfully");
            System.out.println("=============================================================\n");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("Error storing patient data: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to store patient data: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Alternative endpoint that accepts location details directly
     */
    @PostMapping("/patientData/withLocation")
    public ResponseEntity<?> receivePatientDataWithLocation(@RequestBody Map<String, Object> requestBody) {
        System.out.println("\n========== RECEIVING PATIENT DATA WITH LOCATION ==========");
        
        try {
            String name = (String) requestBody.getOrDefault("name", "");
            String symptoms = (String) requestBody.getOrDefault("symptoms", "");
            String predictedDisease = (String) requestBody.getOrDefault("predictedDisease", "");
            int probability = requestBody.get("probability") != null ? 
                ((Number) requestBody.get("probability")).intValue() : 0;
            String pincode = (String) requestBody.getOrDefault("pincode", "");
            String city = (String) requestBody.getOrDefault("city", "");
            String village = (String) requestBody.getOrDefault("village", "");
            String state = (String) requestBody.getOrDefault("state", "");
            String contact = (String) requestBody.getOrDefault("contact", "");
            int age = requestBody.get("age") != null ? ((Number) requestBody.get("age")).intValue() : 0;
            String gender = (String) requestBody.getOrDefault("gender", "");
            int temperature = requestBody.get("temperature") != null ? 
                ((Number) requestBody.get("temperature")).intValue() : 0;
            int days = requestBody.get("days") != null ? ((Number) requestBody.get("days")).intValue() : 0;
            double riskfactor = requestBody.get("riskfactor") != null ? 
                ((Number) requestBody.get("riskfactor")).doubleValue() : 1.0;
            
            System.out.println("Name: " + name + ", City: " + city + ", State: " + state);
            
            // Create Query record
            Query query = new Query();
            query.setName(name);
            query.setSymptoms(symptoms);
            query.setContact(contact);
            query.setAge(age);
            query.setGender(gender);
            query.setTemperature(temperature);
            query.setDays(days);
            query.setRiskfactor(riskfactor);
            query.setAttended(0);
            query.setReceivedAt(Date.from(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toInstant()));
            
            Query savedQuery = queryRepository.save(query);
            
            // Create location
            PatientLocation location = new PatientLocation();
            location.setQueryId(savedQuery.getId());
            location.setPincode(pincode);
            location.setCity(city);
            location.setVillage(village);
            location.setState(state);
            patientLocationRepository.save(location);
            
            // Save prediction
            if (!predictedDisease.isEmpty()) {
                PredictedDisease prediction = new PredictedDisease();
                prediction.setQueryId(savedQuery.getId());
                prediction.setDisease(predictedDisease);
                prediction.setProbability(probability / 100.0);
                predictedDiseaseRepository.save(prediction);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Patient data stored successfully");
            response.put("queryId", savedQuery.getId());
            
            System.out.println("Data stored with Query ID: " + savedQuery.getId());
            System.out.println("========================================================\n");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
