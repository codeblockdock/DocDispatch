import React, { useState } from 'react';
import { patientAPI } from '../services/api';
import './PatientModal.css';

function PatientModal({ patient, onClose, onRefresh }) {
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    doctor: '',
    hospital: '',
    city: '',
    diagnosis: '',
    treatment: '',
    advice: '',
    appointment: '',
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleAttend = async () => {
    setLoading(true);
    try {
      await patientAPI.attend(patient.id, formData);
      onRefresh();
      onClose();
    } catch (err) {
      console.error('Error updating patient:', err);
    }
    setLoading(false);
  };

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

  const getStatusBadge = (status) => {
    const statusMap = {
      'High Risk': 'badge-danger',
      'Medium Risk': 'badge-warning',
      'Attended': 'badge-success',
      'Pending': 'badge-secondary',
    };
    return statusMap[status] || 'badge-secondary';
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
                <span className={`badge ${getStatusBadge(patient.status)}`}>
                  {patient.status}
                </span>
              </div>
            </div>
          </div>

          {/* Details Grid */}
          <div className="details-grid">
            <div className="detail-item">
              <span className="detail-label">Location</span>
              <span className="detail-value">{patient.city}, {patient.state}</span>
            </div>
            <div className="detail-item">
              <span className="detail-label">Pincode</span>
              <span className="detail-value">{patient.pincode || 'N/A'}</span>
            </div>
            <div className="detail-item">
              <span className="detail-label">Temperature</span>
              <span className="detail-value">{patient.temperature}°F</span>
            </div>
            <div className="detail-item">
              <span className="detail-label">Days Sick</span>
              <span className="detail-value">{patient.days} days</span>
            </div>
            <div className="detail-item">
              <span className="detail-label">Contagious</span>
              <span className="detail-value">{patient.contagious}</span>
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
              <div className="prediction-probability">
                <span className="prediction-label">Confidence</span>
                <div className="probability-display">
                  <div className="probability-circle">
                    <svg viewBox="0 0 36 36">
                      <path
                        d="M18 2.0845
                          a 15.9155 15.9155 0 0 1 0 31.831
                          a 15.9155 15.9155 0 0 1 0 -31.831"
                        fill="none"
                        stroke="#e2e8f0"
                        strokeWidth="3"
                      />
                      <path
                        d="M18 2.0845
                          a 15.9155 15.9155 0 0 1 0 31.831
                          a 15.9155 15.9155 0 0 1 0 -31.831"
                        fill="none"
                        stroke={patient.probability >= 70 ? '#ef4444' : patient.probability >= 40 ? '#f59e0b' : '#10b981'}
                        strokeWidth="3"
                        strokeDasharray={`${patient.probability}, 100`}
                      />
                    </svg>
                    <span>{patient.probability}%</span>
                  </div>
                </div>
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

          {/* Attend Form */}
          {patient.attended !== 1 && (
            <div className="attend-section">
              <div className="attend-header">
                <h4>Attend Patient</h4>
                <button 
                  className="btn btn-sm btn-secondary"
                  onClick={() => setIsEditing(!isEditing)}
                >
                  {isEditing ? 'Cancel' : 'Mark as Attended'}
                </button>
              </div>
              
              {isEditing && (
                <div className="attend-form">
                  <div className="form-row">
                    <div className="form-group">
                      <label className="form-label">Doctor Name</label>
                      <input
                        type="text"
                        name="doctor"
                        value={formData.doctor}
                        onChange={handleChange}
                        className="form-input"
                        placeholder="Enter doctor name"
                      />
                    </div>
                    <div className="form-group">
                      <label className="form-label">Hospital</label>
                      <input
                        type="text"
                        name="hospital"
                        value={formData.hospital}
                        onChange={handleChange}
                        className="form-input"
                        placeholder="Enter hospital name"
                      />
                    </div>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Diagnosis</label>
                    <textarea
                      name="diagnosis"
                      value={formData.diagnosis}
                      onChange={handleChange}
                      className="form-input"
                      placeholder="Enter diagnosis"
                      rows="2"
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Treatment</label>
                    <textarea
                      name="treatment"
                      value={formData.treatment}
                      onChange={handleChange}
                      className="form-input"
                      placeholder="Enter treatment details"
                      rows="2"
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Advice</label>
                    <textarea
                      name="advice"
                      value={formData.advice}
                      onChange={handleChange}
                      className="form-input"
                      placeholder="Enter advice for patient"
                      rows="2"
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Next Appointment</label>
                    <input
                      type="datetime-local"
                      name="appointment"
                      value={formData.appointment}
                      onChange={handleChange}
                      className="form-input"
                    />
                  </div>
                  <button 
                    className="btn btn-success"
                    onClick={handleAttend}
                    disabled={loading}
                  >
                    {loading ? 'Saving...' : 'Save & Mark Attended'}
                  </button>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default PatientModal;
