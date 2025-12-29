# Disease Prediction API - Setup Guide

## Overview
The DocDispatch server now includes AI-powered disease prediction! This feature uses a machine learning model to predict possible diseases based on patient symptoms.

## Architecture
```
Flutter App -> Spring Boot API -> Python ML Model -> Predictions
```

## Files Created/Modified

### New Java Files:
1. **PredictDiseaseRequest.java** - DTO for symptom input
2. **DiseasePrediction.java** - DTO for prediction output
3. **PredictionService.java** - Service that calls Python script
4. **QueryController.java** - Added `/api/predict` endpoint

### Python Files:
- **predict.py** - ML model interface script
- **disease_model.pkl** - Trained machine learning model
- **symptom_list.pkl** - List of symptoms model understands

## Prerequisites

### 1. Python Setup
Make sure Python 3.x is installed and accessible from command line:
```bash
python --version
```

### 2. Install Required Python Packages
```bash
pip install joblib pandas scikit-learn
```

## API Endpoint

### POST /api/predict
Predicts diseases based on symptoms.

**Request:**
```json
{
  "symptoms": ["fever", "cough", "headache", "body ache"]
}
```

**Response:**
```json
[
  {
    "disease": "Common Cold",
    "probability": 85.67
  },
  {
    "disease": "Influenza",
    "probability": 78.23
  },
  {
    "disease": "Sinusitis",
    "probability": 45.12
  }
]
```

## Testing

### Using the Test Website
1. Open `Server/Test/index.html` in your browser
2. Find the "🤖 Test Disease Prediction" section
3. Modify symptoms in the JSON textarea
4. Click "Predict Disease"

### Using cURL
```bash
curl -X POST http://localhost:8081/api/predict \
  -H "Content-Type: application/json" \
  -d '{"symptoms": ["fever", "cough", "headache"]}'
```

### From Flutter App
```dart
final response = await http.post(
  Uri.parse('http://YOUR_SERVER:8081/api/predict'),
  headers: {'Content-Type': 'application/json'},
  body: jsonEncode({
    'symptoms': ['fever', 'cough', 'headache']
  }),
);

final predictions = jsonDecode(response.body) as List;
// predictions[0]['disease'], predictions[0]['probability']
```

## Troubleshooting

### Error: "Python script failed"
- **Cause:** Python not found or packages missing
- **Fix:** Install Python and required packages (see Prerequisites)

### Error: "FileNotFoundError: disease_model.pkl"
- **Cause:** Model files not in correct location
- **Fix:** Ensure `disease_model.pkl` and `symptom_list.pkl` are in `Server/` directory

### Error: "Path to predict.py not found"
- **Cause:** Relative path issue in PredictionService.java
- **Fix:** Update the path in PredictionService.java line 41:
  ```java
  "python",
  "C:/DocDispatch/Server/predict.py",  // Use absolute path
  jsonInput
  ```

## How It Works

1. **Patient enters symptoms** in Flutter app
2. **App sends POST request** to `/api/predict` with symptom list
3. **Spring Boot receives request** in QueryController
4. **PredictionService executes Python script** as subprocess
5. **Python script loads ML model** from .pkl files
6. **Model predicts top 3 diseases** with probabilities
7. **Results returned as JSON** to Flutter app
8. **App displays predictions** to patient

## File Locations
```
Server/
├── predict.py              # Python ML script
├── disease_model.pkl       # Trained model
├── symptom_list.pkl        # Symptom features
├── Test/
│   └── index.html         # Updated with prediction test
└── backend-spring-boot/
    └── src/main/java/.../
        ├── controller/
        │   └── QueryController.java      # Added /api/predict endpoint
        ├── service/
        │   └── PredictionService.java    # New service
        └── dto/
            ├── PredictDiseaseRequest.java   # New DTO
            └── DiseasePrediction.java       # New DTO
```

## Next Steps

1. **Train better model:** Improve prediction accuracy with more data
2. **Add symptom validation:** Check if symptoms are valid before prediction
3. **Cache predictions:** Store predictions to avoid redundant ML calls
4. **Add confidence threshold:** Only show diseases above certain probability
5. **Integrate with queries:** Auto-predict disease when patient submits query

---
**Note:** This ML feature is for informational purposes only and should not replace professional medical diagnosis!
