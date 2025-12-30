/*
 * QueryService.java - Business Logic Layer for Query Management
 * 
 * PURPOSE:
 * This service class contains the core business logic for managing patient queries
 * and doctor responses. It acts as an intermediary between the controller and
 * the database repositories.
 * 
 * MAIN OPERATIONS:
 * 
 * 1. addQuery(AddQueryRequest) - Creates a new patient query
 *    - Converts DTO to Entity
 *    - Saves query to database
 *    - Uses @Transactional to ensure data consistency
 * 
 * 2. getQueriesByContact(String) - Retrieves all queries for a phone number
 *    - Fetches queries from database
 *    - Joins with attended records if query was answered
 *    - Formats timestamps for display
 *    - Returns formatted response DTOs
 * 
 * 3. attendQuery(AttendQueryRequest) - Doctor responds to patient query
 *    - Updates query status to attended (1)
 *    - Creates new attended record with doctor's response
 *    - Uses @Transactional to ensure both operations succeed or fail together
 * 
 * TRANSACTION MANAGEMENT:
 * @Transactional ensures database operations are atomic - if any step fails,
 * all changes are rolled back to maintain data integrity
 * 
 * DATA FLOW:
 * Controller -> Service (Business Logic) -> Repository (Database Access)
 */
package online.ppriyanshu26.docdispatch.service;

import online.ppriyanshu26.docdispatch.dto.AddQueryRequest;
import online.ppriyanshu26.docdispatch.dto.AttendQueryRequest;
import online.ppriyanshu26.docdispatch.dto.DiseasePrediction;
import online.ppriyanshu26.docdispatch.dto.QueryResponseDto;
import online.ppriyanshu26.docdispatch.entity.Attended;
import online.ppriyanshu26.docdispatch.entity.PatientLocation;
import online.ppriyanshu26.docdispatch.entity.PredictedDisease;
import online.ppriyanshu26.docdispatch.entity.Query;
import online.ppriyanshu26.docdispatch.repository.AttendedRepository;
import online.ppriyanshu26.docdispatch.repository.PatientLocationRepository;
import online.ppriyanshu26.docdispatch.repository.PredictedDiseaseRepository;
import online.ppriyanshu26.docdispatch.repository.QueryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class QueryService {
    
    private final QueryRepository queryRepository;
    private final AttendedRepository attendedRepository;
    private final PatientLocationRepository patientLocationRepository;
    private final PredictedDiseaseRepository predictedDiseaseRepository;
    private final PredictionService predictionService;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public QueryService(QueryRepository queryRepository, AttendedRepository attendedRepository, 
                       PatientLocationRepository patientLocationRepository,
                       PredictedDiseaseRepository predictedDiseaseRepository,
                       PredictionService predictionService) {
        this.queryRepository = queryRepository;
        this.attendedRepository = attendedRepository;
        this.patientLocationRepository = patientLocationRepository;
        this.predictedDiseaseRepository = predictedDiseaseRepository;
        this.predictionService = predictionService;
        this.objectMapper = new ObjectMapper();
    }
    
    @Transactional
    public void addQuery(AddQueryRequest request) {
        // ===== PRINT RECEIVED DATA TO TERMINAL =====
        System.out.println("\n========== NEW QUERY RECEIVED ==========");
        System.out.println("Contact: " + request.getContact());
        System.out.println("Name: " + request.getName());
        System.out.println("Age: " + request.getAge());
        System.out.println("Gender: " + request.getGender());
        System.out.println("Temperature: " + request.getTemperature());
        System.out.println("Days Sick: " + request.getDays());
        System.out.println("Contagious: " + request.getContagious());
        System.out.println("Symptoms: " + request.getSymptoms());
        if (request.getAddress() != null) {
            System.out.println("Address - Zip: " + request.getAddress().getZip() + 
                             ", City: " + request.getAddress().getCity() + 
                             ", State: " + request.getAddress().getState());
        }
        System.out.println("========================================\n");
        
        // Save query
        Query query = new Query();
        // Normalize contact: remove all non-numeric characters
        String normalizedContact = request.getContact().replaceAll("[^0-9]", "");
        query.setContact(normalizedContact);
        query.setName(request.getName());
        query.setAge(request.getAge());
        query.setGender(request.getGender());
        query.setTemperature(request.getTemperature());
        query.setDays(request.getDays());
        query.setContagious(request.getContagious());
        query.setAttended(0);
        
        // Convert symptoms list to JSON string
        try {
            String symptomsJson = objectMapper.writeValueAsString(request.getSymptoms());
            query.setSymptoms(symptomsJson);
        } catch (Exception e) {
            System.out.println("Error converting symptoms to JSON: " + e.getMessage());
            query.setSymptoms("[]");
        }
        
        Query savedQuery = queryRepository.save(query);
        System.out.println("Query saved with ID: " + savedQuery.getQid());
        
        // Save patient location
        if (request.getAddress() != null) {
            try {
                PatientLocation location = new PatientLocation();
                location.setQid(savedQuery.getQid());
                location.setPincode(request.getAddress().getZip());
                location.setCity(request.getAddress().getCity());
                location.setState(request.getAddress().getState());
                
                patientLocationRepository.save(location);
                System.out.println("Patient location saved for Query ID: " + savedQuery.getQid());
            } catch (Exception e) {
                System.out.println("Error saving patient location: " + e.getMessage());
            }
        }
        
        // Predict disease if symptoms count is 5 or more
        if (request.getSymptoms() != null && request.getSymptoms().size() >= 5) {
            try {
                System.out.println("Symptoms count >= 5, predicting disease...");
                List<DiseasePrediction> predictions = predictionService.predictDisease(request.getSymptoms());
                
                if (predictions != null && !predictions.isEmpty()) {
                    // Get the top predicted disease (first one has highest probability)
                    String topDisease = predictions.get(0).getDisease();
                    String symptomsJson = objectMapper.writeValueAsString(request.getSymptoms());
                    
                    // Save to predicted_disease table
                    PredictedDisease predictedDisease = new PredictedDisease();
                    predictedDisease.setQid(savedQuery.getQid());
                    predictedDisease.setSymptoms(symptomsJson);
                    predictedDisease.setDisease(topDisease);
                    
                    predictedDiseaseRepository.save(predictedDisease);
                    System.out.println("Predicted disease saved: " + topDisease + " for Query ID: " + savedQuery.getQid());
                }
            } catch (Exception e) {
                System.out.println("Error predicting disease: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Symptoms count < 5, skipping disease prediction");
        }
    }
    
    public List<QueryResponseDto> getQueriesByContact(String contact) {
        // Normalize contact: remove all non-numeric characters
        String normalizedContact = contact.replaceAll("[^0-9]", "");
        List<Query> queries = queryRepository.findByContact(normalizedContact);
        List<QueryResponseDto> responses = new ArrayList<>();
        
        for (Query query : queries) {
            QueryResponseDto response = new QueryResponseDto();
            response.setName(query.getName());
            response.setAttended(query.getAttended());
            
            // Set date - handle null case
            String dateStr = "";
            if (query.getReceivedAt() != null) {
                dateStr = query.getReceivedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            response.setDate(dateStr);
            
            if (query.getAttended() == 1) {
                // Query has been attended by a doctor, fetch attended details
                attendedRepository.findByQid(query.getQid()).ifPresent(attended -> {
                    response.setDoctor(attended.getDoctor());
                    response.setHospital(attended.getHospital());
                    response.setCity(attended.getCity());
                    response.setTreatment(attended.getTreatment());
                    response.setDiagnosis(attended.getDiagnosis());
                    response.setAdvice(attended.getAdvice());
                    response.setAppointment(attended.getAppointment());
                });
            } else {
                // Query is pending, set default values for Flutter app
                response.setDoctor("Pending");
                response.setHospital("Unknown Hospital");
                response.setCity("Unknown City");
                response.setTreatment("");
                response.setDiagnosis("Under Observation");
                response.setAdvice("No specific advice");
                response.setAppointment("Not Applicable");
            }
            
            responses.add(response);
        }
        
        return responses;
    }
    
    @Transactional
    public void attendQuery(AttendQueryRequest request) {
        // Update query as attended
        Query query = queryRepository.findById(request.getQid())
            .orElseThrow(() -> new RuntimeException("Query not found"));
        query.setAttended(1);
        queryRepository.save(query);
        
        // Add attended record
        Attended attended = new Attended();
        attended.setQid(request.getQid());
        attended.setContact(request.getContact());
        attended.setDoctor(request.getDoctor());
        attended.setHospital(request.getHospital());
        attended.setCity(request.getCity());
        attended.setDiagnosis(request.getDiagnosis());
        attended.setTreatment(request.getTreatment());
        attended.setAdvice(request.getAdvice() != null ? request.getAdvice() : "");
        attended.setAppointment(request.getAppointment() != null ? request.getAppointment() : "");
        
        attendedRepository.save(attended);
    }
}
