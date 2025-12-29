/*
 * PredictionService.java - Service for Disease Prediction using Python ML Model
 * 
 * PURPOSE:
 * This service bridges the Java Spring Boot backend with the Python machine learning
 * model. It executes the Python script and processes the prediction results.
 * 
 * HOW IT WORKS:
 * 1. Receives list of symptoms from controller
 * 2. Converts symptoms to JSON format
 * 3. Executes Python script (predict.py) as a subprocess
 * 4. Passes symptoms as command-line argument
 * 5. Python script loads ML model and predicts diseases
 * 6. Reads JSON output from Python script
 * 7. Converts JSON to Java objects (DiseasePrediction DTOs)
 * 8. Returns top 3 disease predictions with probabilities
 * 
 * PYTHON INTEGRATION:
 * - Uses ProcessBuilder to execute Python interpreter
 * - Script location: ../../predict.py (relative to backend folder)
 * - Model files: disease_model.pkl, symptom_list.pkl
 * 
 * ERROR HANDLING:
 * - Catches Python execution errors
 * - Returns meaningful error messages if model fails
 * - Logs errors for debugging
 * 
 * REQUIREMENTS:
 * - Python 3.x installed on server
 * - Required Python packages: joblib, pandas, scikit-learn
 * - ML model files must exist in Server directory
 */
package online.ppriyanshu26.docdispatch.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import online.ppriyanshu26.docdispatch.dto.DiseasePrediction;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PredictionService {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
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
}
