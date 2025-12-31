package online.anshu.docdispatch.controller;

import online.anshu.docdispatch.dto.*;
import online.anshu.docdispatch.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hospital")
@CrossOrigin(origins = "*")
public class HospitalController {
    
    private final HospitalService hospitalService;
    
    @Autowired
    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<HospitalLoginResponse> login(@RequestBody HospitalLoginRequest request) {
        HospitalLoginResponse response = hospitalService.login(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<HospitalLoginResponse> register(@RequestBody HospitalRegisterRequest request) {
        HospitalLoginResponse response = hospitalService.register(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
    
    @GetMapping("/patients")
    public ResponseEntity<?> getPatients(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String disease,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String pincode) {
        
        String token = extractToken(authHeader);
        String state = hospitalService.getHospitalStateFromToken(token);
        
        if (state == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(error);
        }
        
        List<PatientDashboardDto> patients = hospitalService.getPatientsByState(state, search, disease, city, pincode);
        return ResponseEntity.ok(patients);
    }
    
    @GetMapping("/patients/{id}")
    public ResponseEntity<?> getPatientById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String id) {
        
        String token = extractToken(authHeader);
        String state = hospitalService.getHospitalStateFromToken(token);
        
        if (state == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(error);
        }
        
        PatientDashboardDto patient = hospitalService.getPatientById(id, state);
        if (patient == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Patient not found or access denied");
            return ResponseEntity.status(404).body(error);
        }
        
        return ResponseEntity.ok(patient);
    }
    
    @DeleteMapping("/patients/{id}")
    public ResponseEntity<?> deletePatient(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String id) {
        
        String token = extractToken(authHeader);
        String state = hospitalService.getHospitalStateFromToken(token);
        
        if (state == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(error);
        }
        
        boolean deleted = hospitalService.deletePatient(id, state);
        if (deleted) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Patient deleted successfully");
            return ResponseEntity.ok(response);
        }
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "Failed to delete patient or access denied");
        return ResponseEntity.status(403).body(error);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        String state = hospitalService.getHospitalStateFromToken(token);
        
        if (state == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(error);
        }
        
        DashboardStatsDto stats = hospitalService.getStatsByState(state);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        String state = hospitalService.getHospitalStateFromToken(token);
        String hospitalId = hospitalService.getHospitalIdFromToken(token);
        
        if (state == null || hospitalId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(error);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("hospitalId", hospitalId);
        response.put("state", state);
        return ResponseEntity.ok(response);
    }
    
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }
}
