package online.anshu.docdispatch.service;

import online.anshu.docdispatch.dto.AddQueryRequest;
import online.anshu.docdispatch.dto.AttendQueryRequest;
import online.anshu.docdispatch.dto.QueryResponseDto;
import online.anshu.docdispatch.entity.Attended;
import online.anshu.docdispatch.entity.PatientLocation;
import online.anshu.docdispatch.entity.Query;
import online.anshu.docdispatch.repository.AttendedRepository;
import online.anshu.docdispatch.repository.PatientLocationRepository;
import online.anshu.docdispatch.repository.QueryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.time.ZonedDateTime;
import java.time.ZoneId;

@Service
public class QueryService {
    
    private final QueryRepository queryRepository;
    private final AttendedRepository attendedRepository;
    private final PatientLocationRepository patientLocationRepository;
    private final PredictionService predictionService;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public QueryService(QueryRepository queryRepository, AttendedRepository attendedRepository, 
                       PatientLocationRepository patientLocationRepository,
                       PredictionService predictionService) {
        this.queryRepository = queryRepository;
        this.attendedRepository = attendedRepository;
        this.patientLocationRepository = patientLocationRepository;
        this.predictionService = predictionService;
        this.objectMapper = new ObjectMapper();
    }
    
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
        query.setReceivedAt(Date.from(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toInstant()));
        
        // Convert symptoms list to JSON string
        try {
            String symptomsJson = objectMapper.writeValueAsString(request.getSymptoms());
            query.setSymptoms(symptomsJson);
        } catch (Exception e) {
            System.out.println("Error converting symptoms to JSON: " + e.getMessage());
            query.setSymptoms("[]");
        }
        
        Query savedQuery = queryRepository.save(query);
        System.out.println("Query saved with ID: " + savedQuery.getId());
        
        // Save patient location
        if (request.getAddress() != null) {
            try {
                PatientLocation location = new PatientLocation();
                location.setQueryId(savedQuery.getId());
                location.setPincode(request.getAddress().getZip());
                location.setCity(request.getAddress().getCity());
                location.setState(request.getAddress().getState());
                
                patientLocationRepository.save(location);
                System.out.println("Patient location saved for Query ID: " + savedQuery.getId());
            } catch (Exception e) {
                System.out.println("Error saving patient location: " + e.getMessage());
            }
        }
        
        // Predict disease if symptoms count is 5 or more
        if (request.getSymptoms() != null && request.getSymptoms().size() >= 5) {
            try {
                System.out.println("Symptoms count >= 5, starting background disease prediction...");
                predictionService.predictAndSaveDisease(request.getSymptoms(), savedQuery.getId());
            } catch (Exception e) {
                System.out.println("Error starting background disease prediction: " + e.getMessage());
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
        
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        
        for (Query query : queries) {
            QueryResponseDto response = new QueryResponseDto();
            response.setName(query.getName());
            response.setAttended(query.getAttended());
            
            // Set date - handle null case
            String dateStr = "";
            if (query.getReceivedAt() != null) {
                dateStr = formatter.format(query.getReceivedAt());
            }
            response.setDate(dateStr);
            
            if (query.getAttended() == 1) {
                // Query has been attended by a doctor, fetch attended details
                attendedRepository.findByQueryId(query.getId()).ifPresent(attended -> {
                    response.setDoctor(attended.getDoctor());
                    response.setHospital(attended.getHospital());
                    response.setCity(attended.getCity());
                    response.setTreatment(attended.getTreatment());
                    response.setDiagnosis(attended.getDiagnosis());
                    response.setAdvice(attended.getAdvice());
                    response.setAppointment(attended.getAppointment());
                    
                    // Override date with attended timestamp
                    if (attended.getTimestamp() != null) {
                        response.setDate(formatter.format(attended.getTimestamp()));
                    }
                });
            } else {
                // Query is pending, set empty values for mobile app
                response.setDoctor("");
                response.setHospital("");
                response.setCity("");
                response.setTreatment("");
                response.setDiagnosis("");
                response.setAdvice("");
                response.setAppointment("");
            }
            
            responses.add(response);
        }
        
        return responses;
    }
    
    public void attendQuery(AttendQueryRequest request) {
        // Update query as attended
        Query query = queryRepository.findById(request.getQueryId())
            .orElseThrow(() -> new RuntimeException("Query not found"));
        query.setAttended(1);
        queryRepository.save(query);
        
        // Add attended record
        Attended attended = new Attended();
        attended.setQueryId(request.getQueryId());
        attended.setDoctor(request.getDoctor());
        attended.setHospital(request.getHospital());
        attended.setCity(request.getCity());
        attended.setDiagnosis(request.getDiagnosis());
        attended.setTreatment(request.getTreatment());
        attended.setAdvice(request.getAdvice() != null ? request.getAdvice() : "");
        attended.setAppointment(request.getAppointment() != null ? request.getAppointment() : "");
        attended.setTimestamp(Date.from(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toInstant()));
        
        attendedRepository.save(attended);
    }
}