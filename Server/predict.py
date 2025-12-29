"""
predict.py - Machine Learning Disease Prediction Script

PURPOSE:
This Python script uses a trained machine learning model to predict possible diseases
based on patient symptoms. It's called from the Java Spring Boot backend as a subprocess.

HOW IT WORKS:
1. Receives symptoms as JSON via command-line argument from Java
2. Loads pre-trained ML model (disease_model.pkl)
3. Loads symptom feature list (symptom_list.pkl)
4. Converts symptoms to binary feature vector
5. Predicts disease probabilities using the trained model
6. Returns top 3 diseases with highest probabilities as JSON

INPUT FORMAT (from Java):
sys.argv[1] = '{"symptoms": ["fever", "cough", "headache"]}'

OUTPUT FORMAT (to Java):
[
  {"disease": "Common Cold", "probability": 85.67},
  {"disease": "Influenza", "probability": 78.23},
  {"disease": "Sinusitis", "probability": 45.12}
]

MODEL FILES:
- disease_model.pkl: Trained scikit-learn classifier
- symptom_list.pkl: List of all symptoms the model was trained on

REQUIREMENTS:
- Python 3.x
- joblib (for loading .pkl files)
- pandas (for DataFrame operations)
- scikit-learn (for the ML model)

INTEGRATION:
Called by PredictionService.java using ProcessBuilder
"""

import sys
import json
import joblib
import pandas as pd
import os

# Get the directory where this script is located
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))

# Load model + symptom list using absolute paths
model = joblib.load(os.path.join(SCRIPT_DIR, "disease_model.pkl"))
symptoms = joblib.load(os.path.join(SCRIPT_DIR, "symptom_list.pkl"))

def predict_disease(user_symptoms):
    user_symptoms = [s.lower().strip() for s in user_symptoms]

    # Create input row with correct feature order
    input_df = pd.DataFrame(0, index=[0], columns=symptoms)
    for s in user_symptoms:
        if s in input_df.columns:
            input_df.at[0, s] = 1

    probs = model.predict_proba(input_df)[0]
    diseases = model.classes_

    top = sorted(zip(diseases, probs), key=lambda x: x[1], reverse=True)[:3]
    return [{"disease": d, "probability": round(p*100, 2)} for d, p in top]

# Read symptoms from Java (via stdin for reliable JSON passing on Windows)
if len(sys.argv) > 1:
    # Command line argument mode
    data = json.loads(sys.argv[1])
else:
    # Stdin mode (used by Java ProcessBuilder)
    data = json.loads(sys.stdin.read())

result = predict_disease(data["symptoms"])
print(json.dumps(result))
