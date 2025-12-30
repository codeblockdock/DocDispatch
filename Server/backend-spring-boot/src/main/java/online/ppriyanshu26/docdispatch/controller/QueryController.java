/*
 * QueryController.java - REST API Controller for Patient Query Management
 * 
 * PURPOSE:
 * This controller handles all HTTP requests from the Flutter mobile app related to
 * patient medical queries. It exposes RESTful endpoints for creating, retrieving,
 * and managing patient queries and doctor responses.
 * 
 * ENDPOINTS:
 * 1. POST /api/patient - Submit a new patient query
 *    - Accepts patient information (name, contact, age, symptoms)
 *    - Returns success/error status
 * 
 * 2. GET /api/queries?contact={phone} - Retrieve all queries for a contact
 *    - Fetches patient's query history
 *    - Returns list of queries with attendance status
 * 
 * 3. POST /api/attend - Doctor responds to a patient query
 *    - Marks query as attended
 *    - Stores doctor's diagnosis and treatment
 * 
 * 4. POST /api/predict - Predict disease based on symptoms
 *    - Accepts list of symptoms
 *    - Uses Python ML model to predict top 3 possible diseases
 *    - Returns disease names with probability scores
 * 
 * CORS CONFIGURATION:
 * @CrossOrigin allows requests from any origin (Flutter app, test website)
 * This is necessary for the mobile app to communicate with the server
 * 
 * ERROR HANDLING:
 * All endpoints catch exceptions and return appropriate HTTP status codes
 * with error messages in JSON format
 */
package online.ppriyanshu26.docdispatch.controller;

import online.ppriyanshu26.docdispatch.dto.AddQueryRequest;
import online.ppriyanshu26.docdispatch.dto.AttendQueryRequest;
import online.ppriyanshu26.docdispatch.dto.DiseasePrediction;
import online.ppriyanshu26.docdispatch.dto.PredictDiseaseRequest;
import online.ppriyanshu26.docdispatch.dto.QueryResponseDto;
import online.ppriyanshu26.docdispatch.service.PredictionService;
import online.ppriyanshu26.docdispatch.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("")
@CrossOrigin(origins = "*")
public class QueryController {
    
    private final QueryService queryService;
    private final PredictionService predictionService;
    
    @Autowired
    public QueryController(QueryService queryService, PredictionService predictionService) {
        this.queryService = queryService;
        this.predictionService = predictionService;
    }
    
    @PostMapping("/submit")
    public ResponseEntity<Map<String, String>> addQuery(@RequestBody AddQueryRequest request) {
        try {
            queryService.addQuery(request);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/queries")
    public ResponseEntity<?> getQueries(@RequestParam String contact) {
        System.out.println("\n========== QUERY REQUEST ==========");
        System.out.println("Searching queries for contact: " + contact);
        System.out.println("===================================\n");
        try {
            List<QueryResponseDto> queries = queryService.getQueriesByContact(contact);
            System.out.println("Found " + queries.size() + " queries for contact: " + contact + "\n");
            return ResponseEntity.ok(queries);
        } catch (Exception e) {
            System.out.println("Error fetching queries: " + e.getMessage() + "\n");
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping("/attend")
    public ResponseEntity<Map<String, String>> attendQuery(@RequestBody AttendQueryRequest request) {
        try {
            queryService.attendQuery(request);
            Map<String, String> response = new HashMap<>();
            response.put("status", "attended");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping("/predict")
    public ResponseEntity<?> predictDisease(@RequestBody PredictDiseaseRequest request) {
        try {
            List<DiseasePrediction> predictions = predictionService.predictDisease(request.getSymptoms());
            return ResponseEntity.ok(predictions);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Prediction failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
