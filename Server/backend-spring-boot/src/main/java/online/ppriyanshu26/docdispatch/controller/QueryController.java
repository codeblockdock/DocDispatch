package main.java.online.ppriyanshu26.docdispatch.controller;

import main.java.online.ppriyanshu26.docdispatch.dto.AddQueryRequest;
import main.java.online.ppriyanshu26.docdispatch.dto.AttendQueryRequest;
import main.java.online.ppriyanshu26.docdispatch.dto.QueryResponse;
import main.java.online.ppriyanshu26.docdispatch.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class QueryController {
    
    private final QueryService queryService;
    
    @Autowired
    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }
    
    @PostMapping("/patient")
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
        try {
            List<QueryResponse> queries = queryService.getQueriesByContact(contact);
            return ResponseEntity.ok(queries);
        } catch (Exception e) {
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
}
