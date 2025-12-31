# Hospital Data Portal

An interactive hospital data portal that stores and displays medical prediction data. Hospitals can only view patient data based on their registered state (state-based access control).

## 🏗️ Project Structure

```
DocDispatch/
├── React/                          # React Frontend
│   ├── public/
│   │   └── index.html
│   ├── src/
│   │   ├── components/             # Reusable UI components
│   │   │   ├── Header.js/css
│   │   │   ├── StatsCards.js/css
│   │   │   ├── PatientTable.js/css
│   │   │   ├── PatientModal.js/css
│   │   │   └── DeleteConfirmModal.js/css
│   │   ├── context/
│   │   │   └── AuthContext.js      # Authentication context
│   │   ├── pages/
│   │   │   ├── Login.js/css        # Hospital login page
│   │   │   └── Dashboard.js/css    # Main dashboard
│   │   ├── services/
│   │   │   └── api.js              # API service layer
│   │   ├── App.js
│   │   ├── index.js
│   │   └── index.css
│   └── package.json
│
└── Server/
    └── backend-spring-boot/        # Spring Boot Backend
        ├── src/main/java/online/anshu/docdispatch/
        │   ├── controller/
        │   │   ├── HospitalController.java    # Hospital auth & dashboard APIs
        │   │   ├── PatientDataController.java # External app API
        │   │   └── QueryController.java       # Existing query APIs
        │   ├── dto/
        │   │   ├── HospitalLoginRequest.java
        │   │   ├── HospitalLoginResponse.java
        │   │   ├── HospitalRegisterRequest.java
        │   │   ├── PatientDataRequest.java
        │   │   ├── PatientDashboardDto.java
        │   │   ├── DashboardStatsDto.java
        │   │   └── UpdatePatientRequest.java
        │   ├── entity/
        │   │   ├── Hospital.java              # Hospital entity
        │   │   ├── PatientLocation.java       # Location mapping
        │   │   ├── Query.java                 # Patient queries
        │   │   └── PredictedDisease.java      # Disease predictions
        │   ├── repository/
        │   │   ├── HospitalRepository.java
        │   │   ├── PatientLocationRepository.java
        │   │   ├── QueryRepository.java
        │   │   └── PredictedDiseaseRepository.java
        │   └── service/
        │       ├── HospitalService.java       # Hospital business logic
        │       └── QueryService.java
        └── pom.xml
```

## 🚀 Features

### 🔐 Login & Access Control
- Hospital login with Hospital ID + Password
- Automatic state detection from hospital profile
- State-based patient record filtering
- Hospitals cannot see data from other states

### 📍 Location-Based Filtering
- Patient records linked to locations via `patient_locations` collection
- State-based access control at API level
- Queries filtered by: `Hospital.state === patient_locations.state`

### 📥 External App API
- `POST /api/patientData` - Receive prediction data from external apps
- `POST /api/patientData/withLocation` - Receive data with full location details

### 📊 Hospital Dashboard
- **Stats Cards**: Total patients, High-risk cases, Newly reported, Emergency priority
- **Patient Table**: Name, Symptoms, Disease, Probability, City, State, Status
- **Search & Filters**: Name, disease, city, pincode
- **Actions**: View details, Update status, Delete records

## 🛠️ Setup Instructions

### Prerequisites
- Java 17+
- Maven 3.6+
- Node.js 18+
- MongoDB Atlas or local MongoDB instance

### Backend Setup

1. Navigate to the Spring Boot backend:
   ```bash
   cd Server/backend-spring-boot
   ```

2. Create a `.env` file:
   ```env
   SERVER_PORT=8080
   MONGO_URI=mongodb+srv://<username>:<password>@<cluster>.mongodb.net/<database>
   ```

3. Build and run:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

### Frontend Setup

1. Navigate to the React frontend:
   ```bash
   cd React
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Create a `.env` file (optional):
   ```env
   REACT_APP_API_URL=http://localhost:8080
   ```

4. Start the development server:
   ```bash
   npm start
   ```

The app will be available at `http://localhost:3000`

## 📋 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/hospital/login` | Hospital login |
| POST | `/api/hospital/register` | Register new hospital |
| GET | `/api/hospital/verify` | Verify auth token |

### Patient Data (Protected)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/hospital/patients` | Get patients (state-filtered) |
| GET | `/api/hospital/patients/:id` | Get patient details |
| DELETE | `/api/hospital/patients/:id` | Delete patient record |
| GET | `/api/hospital/stats` | Get dashboard statistics |

### External App Integration
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/patientData` | Receive prediction data |
| POST | `/api/patientData/withLocation` | Receive data with location |

## 📦 MongoDB Collections

### hospitals
```json
{
  "_id": "ObjectId",
  "hospitalId": "HOSP001",
  "name": "City Hospital",
  "password": "hashed_password",
  "state": "Madhya Pradesh",
  "city": "Bhopal",
  "address": "123 Main St",
  "phone": "9876543210",
  "email": "hospital@example.com",
  "active": true,
  "createdAt": "ISODate",
  "lastLogin": "ISODate"
}
```

### patient_locations
```json
{
  "_id": "query_id",
  "queryId": "query_id",
  "pincode": "462042",
  "city": "Bhopal",
  "state": "Madhya Pradesh"
}
```

### queries
```json
{
  "_id": "ObjectId",
  "contact": "9876543210",
  "name": "Patient Name",
  "age": 35,
  "gender": "Male",
  "temperature": 101,
  "days": 3,
  "contagious": "Yes",
  "symptoms": "[\"fever\", \"cough\", \"headache\"]",
  "attended": 0,
  "receivedAt": "ISODate"
}
```

### predicted_diseases
```json
{
  "_id": "query_id",
  "queryId": "query_id",
  "symptoms": "fever, cough",
  "disease": "Common Cold",
  "probability": 0.87
}
```

## 🔒 Security Notes

- Passwords are stored in plain text (for demo). In production, use BCrypt.
- Token-based authentication using Base64 encoding (for demo). In production, use JWT.
- All patient endpoints require valid authentication token.
- State-based filtering enforced at service layer.

## 📝 Sample API Requests

### Hospital Login
```bash
curl -X POST http://localhost:8080/api/hospital/login \
  -H "Content-Type: application/json" \
  -d '{"hospitalId": "HOSP001", "password": "password123"}'
```

### Get Patients (with auth)
```bash
curl http://localhost:8080/api/hospital/patients \
  -H "Authorization: Bearer <token>"
```

### Send Patient Data (External App)
```bash
curl -X POST http://localhost:8080/api/patientData \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "symptoms": "fever, cough, headache",
    "predictedDisease": "Common Cold",
    "probability": 87,
    "location_id": "69555a242b6256a404826359"
  }'
```

## 🎨 Screenshots

### Login Page
- Clean, modern login interface
- Hospital ID + Password authentication
- Registration form with state selection

### Dashboard
- Stats cards showing key metrics
- Filterable patient table
- Patient detail modal with full information
- Attend patient functionality

## 📄 License

This project is part of DocDispatch - Medical Data Management System.
