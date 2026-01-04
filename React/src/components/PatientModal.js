import React from 'react';
import './PatientModal.css';

function PatientModal({ patient, onClose }) {

  const parseSymptoms = (symptoms) => {
    if (!symptoms) return [];
    try {
      const parsed = JSON.parse(symptoms);
      if (Array.isArray(parsed)) return parsed;
      return [symptoms];
    } catch {
      return symptoms.split(',').map(s => s.trim());
    }
  };

  const getRiskBadge = (risk, age) => {
    if ((age >= 5 && age <= 12) || (age >= 51 && age <= 60)) return 'badge-purple';
    if (risk === 3.0) return 'badge-danger';
    if (risk === 1.5) return 'badge-orange';
    if (risk === 1.0) return 'badge-warning';
    return 'badge-secondary';
  };

  const getRiskLabel = (risk, age) => {
    let labels = [];
    if (risk === 3.0) labels.push('High');
    else if (risk === 1.5) labels.push('Medium');
    else if (risk === 1.0) labels.push('Low');
    
    if ((age >= 5 && age <= 12) || (age >= 51 && age <= 60)) {
      labels.push('Priority');
    }
    
    return labels.length > 0 ? labels.join(' + ') : 'N/A';
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal patient-modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-title">Patient Details</h2>
          <button className="modal-close" onClick={onClose}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
        
        <div className="modal-body">
          {/* Patient Info Section */}
          <div className="patient-info-section">
            <div className="patient-info-header">
              <div className="patient-avatar-large">
                {patient.name?.charAt(0).toUpperCase() || 'P'}
              </div>
              <div className="patient-info-main">
                <h3>{patient.name}</h3>
                <p className="patient-meta">
                  {patient.age} years • {patient.gender} • {patient.contact}
                </p>
              </div>
            </div>
          </div>

          {/* Details Grid */}
          <div className="details-grid">
            <div className="detail-item">
              <span className="detail-label">Location</span>
              <span className="detail-value">
                {patient.village ? `${patient.village}, ` : ''}{patient.city}, {patient.state}
              </span>
            </div>
            <div className="detail-item">
              <span className="detail-label">Pincode</span>
              <span className="detail-value">{patient.pincode || 'N/A'}</span>
            </div>
            <div className="detail-item">
              <span className="detail-label">Temperature</span>
              <span className="detail-value">{patient.temperature}°C</span>
            </div>
            <div className="detail-item">
              <span className="detail-label">Days Sick</span>
              <span className="detail-value">{patient.days} days</span>
            </div>
            <div className="detail-item">
              <span className="detail-label">Risk Factor</span>
              <span className={`badge ${getRiskBadge(patient.riskfactor, patient.age)}`}>
                {getRiskLabel(patient.riskfactor, patient.age)}
              </span>
            </div>
            <div className="detail-item">
              <span className="detail-label">Received At</span>
              <span className="detail-value">{patient.receivedAt}</span>
            </div>
          </div>

          {/* Prediction Section */}
          <div className="prediction-section">
            <h4>Disease Prediction</h4>
            <div className="prediction-card">
              <div className="prediction-disease">
                <span className="prediction-label">Predicted Disease</span>
                <span className="prediction-value">
                  {patient.predictedDisease || 'Not yet predicted'}
                </span>
              </div>
            </div>
          </div>

          {/* Symptoms Section */}
          <div className="symptoms-section">
            <h4>Symptoms</h4>
            <div className="symptoms-list">
              {parseSymptoms(patient.symptoms).map((symptom, i) => (
                <span key={i} className="symptom-chip">{symptom}</span>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default PatientModal;
