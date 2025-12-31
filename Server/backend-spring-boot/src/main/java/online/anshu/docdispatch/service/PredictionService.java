package online.anshu.docdispatch.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import online.anshu.docdispatch.dto.DiseasePrediction;
import online.anshu.docdispatch.entity.PredictedDisease;
import online.anshu.docdispatch.repository.PredictedDiseaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PredictionService {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PredictedDiseaseRepository predictedDiseaseRepository;
    
    @Autowired
    public PredictionService(PredictedDiseaseRepository predictedDiseaseRepository) {
        this.predictedDiseaseRepository = predictedDiseaseRepository;
    }
    
    public List<DiseasePrediction> predictDisease(List<String> symptoms) throws Exception {
        // Prepare JSON input for Python script
        Map<String, List<String>> input = new HashMap<>();
        input.put("symptoms", symptoms);
        String jsonInput = objectMapper.writeValueAsString(input);
        
        // Build Python command (no args - we'll pass JSON via stdin)
        ProcessBuilder processBuilder = new ProcessBuilder(
            "python",
            "C:\\DocDispatch\\Server\\predict.py"
        );
        
        // Don't redirect error stream - we only want stdout (JSON output)
        processBuilder.redirectErrorStream(false);
        
        // Execute Python script
        Process process = processBuilder.start();
        
        // Write JSON to Python's stdin
        process.getOutputStream().write(jsonInput.getBytes());
        process.getOutputStream().close();
        
        // Read stdout (JSON output)
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream())
        );
        
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }
        
        // Read stderr for error messages
        BufferedReader errorReader = new BufferedReader(
            new InputStreamReader(process.getErrorStream())
        );
        StringBuilder errorOutput = new StringBuilder();
        while ((line = errorReader.readLine()) != null) {
            errorOutput.append(line);
        }
        
        int exitCode = process.waitFor();
        
        if (exitCode != 0) {
            throw new RuntimeException("Python script failed with exit code " + exitCode + ": " + errorOutput.toString());
        }
        
        // Parse JSON output from Python
        String jsonOutput = output.toString().trim();
        List<DiseasePrediction> predictions = objectMapper.readValue(
            jsonOutput, 
            new TypeReference<List<DiseasePrediction>>() {}
        );
        
        return predictions;
    }
    
    @Async
    public void predictAndSaveDisease(List<String> symptoms, String queryId) {
        try {
            System.out.println("Starting background disease prediction for Query ID: " + queryId);
            List<DiseasePrediction> predictions = predictDisease(symptoms);
            
            if (predictions != null && !predictions.isEmpty()) {
                // Get the top predicted disease (first one has highest probability)
                String topDisease = predictions.get(0).getDisease();
                String symptomsJson = objectMapper.writeValueAsString(symptoms);
                
                // Save to predicted_disease collection
                PredictedDisease predictedDisease = new PredictedDisease();
                predictedDisease.setQueryId(queryId);
                predictedDisease.setSymptoms(symptomsJson);
                predictedDisease.setDisease(topDisease);
                
                predictedDiseaseRepository.save(predictedDisease);
                System.out.println("Predicted disease saved asynchronously: " + topDisease + " for Query ID: " + queryId);
            }
        } catch (Exception e) {
            System.out.println("Error in background disease prediction for Query ID " + queryId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
