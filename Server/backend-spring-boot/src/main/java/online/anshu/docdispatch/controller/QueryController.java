package online.anshu.docdispatch.controller;

import online.anshu.docdispatch.dto.AddQueryRequest;
import online.anshu.docdispatch.dto.AttendQueryRequest;
import online.anshu.docdispatch.dto.DiseasePrediction;
import online.anshu.docdispatch.dto.MassAttendRequest;
import online.anshu.docdispatch.dto.PredictDiseaseRequest;
import online.anshu.docdispatch.dto.QueryResponseDto;
import online.anshu.docdispatch.service.PredictionService;
import online.anshu.docdispatch.service.QueryService;
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
    
    @PostMapping("/mass-attend")
    public ResponseEntity<Map<String, Object>> massAttendQueries(@RequestBody MassAttendRequest request) {
        try {
            int attendedCount = queryService.massAttendQueries(request);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Doctor dispatched in your location");
            response.put("attendedCount", attendedCount);
            response.put("location", request.getLocation());
            response.put("pincode", request.getPincode());
            response.put("doctorsDispatched", request.getDoctorsDispatched());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping("/unattend")
    public ResponseEntity<Map<String, String>> unattendQuery(@RequestParam String queryId) {
        try {
            queryService.unattendQuery(queryId);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Attendance undone successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
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
