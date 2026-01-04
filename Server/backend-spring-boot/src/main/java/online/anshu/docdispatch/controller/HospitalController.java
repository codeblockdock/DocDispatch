package online.anshu.docdispatch.controller;

import online.anshu.docdispatch.dto.*;
import online.anshu.docdispatch.service.HospitalService;
import online.anshu.docdispatch.entity.Hospital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/hospital")
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
    
    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestBody Map<String, String> request) {
        String hospitalId = request.get("hospitalId");
        String token = request.get("token");
        
        Optional<Hospital> hospitalOpt = hospitalService.findByHospitalId(hospitalId);
        
        if (hospitalOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new HashMap<String, Object>() {{
                put("success", false);
                put("message", "Invalid Hospital ID or Token");
            }});
        }
        
        Hospital hospital = hospitalOpt.get();
        
        // Check if token matches
        if (hospital.getToken() == null || !hospital.getToken().equals(token)) {
            return ResponseEntity.badRequest().body(new HashMap<String, Object>() {{
                put("success", false);
                put("message", "Invalid Hospital ID or Token");
            }});
        }
        
        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("success", true);
            put("hospital", new HashMap<String, String>() {{
                put("id", hospital.getHospitalId());
                put("name", hospital.getName());
                put("city", hospital.getCity());
            }});
        }});
    }
    
    @PostMapping("/confirm-registration")
    public ResponseEntity<?> confirmRegistration(@RequestBody Map<String, String> request) {
        String hospitalId = request.get("hospitalId");
        String token = request.get("token");
        String password = request.get("password");
        
        Optional<Hospital> hospitalOpt = hospitalService.findByHospitalId(hospitalId);
        
        if (hospitalOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new HashMap<String, Object>() {{
                put("success", false);
                put("message", "Hospital not found");
            }});
        }
        
        Hospital hospital = hospitalOpt.get();
        
        // Verify token
        if (hospital.getToken() == null || !hospital.getToken().equals(token)) {
            return ResponseEntity.badRequest().body(new HashMap<String, Object>() {{
                put("success", false);
                put("message", "Invalid token");
            }});
        }
        
        // Check if password already set (not empty)
        if (hospital.getPassword() != null && !hospital.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body(new HashMap<String, Object>() {{
                put("success", false);
                put("message", "Hospital already registered");
            }});
        }
        
        // Set password, delete token, and update last login
        hospital.setPassword(password);
        hospital.setToken(null);
        hospital.setLastLogin(new Date());
        hospitalService.saveHospital(hospital);
        
        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("success", true);
            put("message", "Registration confirmed successfully");
        }});
    }
    
    @PostMapping("/complete-registration")
    public ResponseEntity<?> completeRegistration(@RequestBody Map<String, String> request) {
        String hospitalId = request.get("hospitalId");
        String token = request.get("token");
        String password = request.get("password");
        
        Optional<Hospital> hospitalOpt = hospitalService.findByHospitalId(hospitalId);
        
        if (hospitalOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new HashMap<String, Object>() {{
                put("success", false);
                put("message", "Hospital not found");
            }});
        }
        
        Hospital hospital = hospitalOpt.get();
        
        // Verify token
        if (hospital.getToken() == null || !hospital.getToken().equals(token)) {
            return ResponseEntity.badRequest().body(new HashMap<String, Object>() {{
                put("success", false);
                put("message", "Invalid token");
            }});
        }
        
        // Check if password already set (not empty)
        if (hospital.getPassword() != null && !hospital.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body(new HashMap<String, Object>() {{
                put("success", false);
                put("message", "Hospital already registered");
            }});
        }
        
        // Update password and last login
        hospital.setPassword(password);
        hospital.setLastLogin(new Date());
        hospitalService.saveHospital(hospital);
        
        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("success", true);
            put("message", "Registration completed successfully");
        }});
    }
    
    @GetMapping("/patients")
    public ResponseEntity<?> getPatients(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String disease,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String pincode,
            @RequestParam(required = false) String riskFactor) {
        
        String token = extractToken(authHeader);
        String state = hospitalService.getHospitalStateFromToken(token);
        String hospitalId = hospitalService.getHospitalIdFromToken(token);
        
        if (state == null || hospitalId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(error);
        }
        
        Optional<Hospital> hospitalOpt = hospitalService.findByHospitalId(hospitalId);
        if (hospitalOpt.isPresent() && hospitalOpt.get().isAdmin()) {
            List<PatientDashboardDto> patients = hospitalService.getAllPatients(search, disease, city, pincode, riskFactor);
            return ResponseEntity.ok(patients);
        }
        
        // For non-admin hospitals, filter by their assigned region (pincodes)
        if (hospitalOpt.isPresent()) {
            Hospital hospital = hospitalOpt.get();
            List<String> regionPincodes = hospital.getRegion();
            if (regionPincodes != null && !regionPincodes.isEmpty()) {
                List<PatientDashboardDto> patients = hospitalService.getPatientsByRegion(regionPincodes, search, disease, city, pincode, riskFactor);
                return ResponseEntity.ok(patients);
            }
        }
        
        // Fallback to state-based filtering if no region is assigned
        List<PatientDashboardDto> patients = hospitalService.getPatientsByState(state, search, disease, city, pincode, riskFactor);
        return ResponseEntity.ok(patients);
    }
    
    @GetMapping("/patients/{id}")
    public ResponseEntity<?> getPatientById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String id) {
        
        String token = extractToken(authHeader);
        String state = hospitalService.getHospitalStateFromToken(token);
        String hospitalId = hospitalService.getHospitalIdFromToken(token);
        
        if (state == null || hospitalId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(error);
        }
        
        // Check if admin - admins can access all patients
        Optional<Hospital> hospitalOpt = hospitalService.findByHospitalId(hospitalId);
        PatientDashboardDto patient;
        if (hospitalOpt.isPresent() && hospitalOpt.get().isAdmin()) {
            patient = hospitalService.getPatientByIdForAdmin(id);
        } else {
            patient = hospitalService.getPatientById(id, state);
        }
        
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
        String hospitalId = hospitalService.getHospitalIdFromToken(token);
        
        if (state == null || hospitalId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(error);
        }
        
        // Check if admin - admins can delete any patient
        Optional<Hospital> hospitalOpt = hospitalService.findByHospitalId(hospitalId);
        boolean deleted;
        if (hospitalOpt.isPresent() && hospitalOpt.get().isAdmin()) {
            deleted = hospitalService.deletePatientForAdmin(id);
        } else {
            deleted = hospitalService.deletePatient(id, state);
        }
        
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
        String hospitalId = hospitalService.getHospitalIdFromToken(token);
        
        if (state == null || hospitalId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(error);
        }
        
        // Check if admin - admins see stats for all patients
        Optional<Hospital> hospitalOpt = hospitalService.findByHospitalId(hospitalId);
        DashboardStatsDto stats;
        if (hospitalOpt.isPresent() && hospitalOpt.get().isAdmin()) {
            stats = hospitalService.getAllStats();
        } else if (hospitalOpt.isPresent()) {
            // For non-admin hospitals, get stats by their assigned region (pincodes)
            Hospital hospital = hospitalOpt.get();
            List<String> regionPincodes = hospital.getRegion();
            if (regionPincodes != null && !regionPincodes.isEmpty()) {
                stats = hospitalService.getStatsByRegion(regionPincodes);
            } else {
                // Fallback to state-based stats if no region is assigned
                stats = hospitalService.getStatsByState(state);
            }
        } else {
            stats = hospitalService.getStatsByState(state);
        }
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/all-hospital-stats")
    public ResponseEntity<?> getAllHospitalStats(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        String hospitalId = hospitalService.getHospitalIdFromToken(token);
        
        if (hospitalId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(error);
        }
        
        Optional<Hospital> hospitalOpt = hospitalService.findByHospitalId(hospitalId);
        if (hospitalOpt.isPresent() && hospitalOpt.get().isAdmin()) {
            List<HospitalStatsDto> stats = hospitalService.getHospitalStatsList();
            return ResponseEntity.ok(stats);
        }
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "Access denied: Admin only");
        return ResponseEntity.status(403).body(error);
    }

    @PostMapping("/toggle-status/{hospitalId}")
    public ResponseEntity<?> toggleHospitalStatus(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String hospitalId) {
        
        String token = extractToken(authHeader);
        String adminId = hospitalService.getHospitalIdFromToken(token);
        
        if (adminId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(error);
        }
        
        Optional<Hospital> adminOpt = hospitalService.findByHospitalId(adminId);
        if (adminOpt.isPresent() && adminOpt.get().isAdmin()) {
            boolean success = hospitalService.toggleHospitalStatus(hospitalId);
            if (success) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Hospital status updated successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Hospital not found");
                return ResponseEntity.status(404).body(error);
            }
        }
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "Access denied: Admin only");
        return ResponseEntity.status(403).body(error);
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
