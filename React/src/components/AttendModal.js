import React, { useState, useEffect } from 'react';
import { patientAPI } from '../services/api';
import './AttendModal.css';

// List of Indian states and union territories
const INDIAN_STATES = [
  'Andhra Pradesh',
  'Arunachal Pradesh',
  'Assam',
  'Bihar',
  'Chhattisgarh',
  'Goa',
  'Gujarat',
  'Haryana',
  'Himachal Pradesh',
  'Jharkhand',
  'Karnataka',
  'Kerala',
  'Madhya Pradesh',
  'Maharashtra',
  'Manipur',
  'Meghalaya',
  'Mizoram',
  'Nagaland',
  'Odisha',
  'Punjab',
  'Rajasthan',
  'Sikkim',
  'Tamil Nadu',
  'Telangana',
  'Tripura',
  'Uttar Pradesh',
  'Uttarakhand',
  'West Bengal',
  'Andaman and Nicobar Islands',
  'Chandigarh',
  'Dadra and Nagar Haveli and Daman and Diu',
  'Delhi',
  'Jammu and Kashmir',
  'Ladakh',
  'Lakshadweep',
  'Puducherry',
];

function AttendModal({ patient, onClose, onRefresh }) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [hospitalData, setHospitalData] = useState({
    doctorName: '',
    city: '',
    state: '',
    hospitalName: '',
  });
  const [formData, setFormData] = useState({
    diagnosis: '',
    treatment: '',
    appointmentDate: '',
    appointmentTime: '',
    advice: '',
  });

  useEffect(() => {
    // Fetch hospital data from localStorage
    const storedData = localStorage.getItem('hospitalData');
    if (storedData) {
      try {
        const data = JSON.parse(storedData);
        setHospitalData({
          doctorName: data.doctorName || '',
          city: data.city || '',
          state: data.state || '',
          hospitalName: data.name || data.hospitalName || '',
        });
      } catch (e) {
        console.error('Error parsing hospital data:', e);
      }
    }
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleDoctorChange = (e) => {
    const { name, value } = e.target;
    setHospitalData(prev => ({ ...prev, [name]: value }));
  };

  const formatDateTimeToISO = (dateStr, timeStr) => {
    if (!dateStr || !timeStr) return '';
    
    // dateStr is in YYYY-MM-DD format from date input
    // timeStr is in HH:MM format from time input
    return `${dateStr}T${timeStr}:00`;
  };

  // Get minimum date (today) for the date picker
  const getMinDate = () => {
    const today = new Date();
    return today.toISOString().split('T')[0];
  };

  const handleAttend = async () => {
    if (!hospitalData.doctorName.trim()) {
      setError('Doctor name is required');
      return;
    }
    if (!hospitalData.city.trim()) {
      setError('City is required');
      return;
    }
    if (!hospitalData.state.trim()) {
      setError('State is required');
      return;
    }
    if (!hospitalData.hospitalName.trim()) {
      setError('Hospital name is required');
      return;
    }

    setLoading(true);
    setError('');
    
    try {
      const appointmentValue = formData.appointmentDate && formData.appointmentTime 
        ? formatDateTimeToISO(formData.appointmentDate, formData.appointmentTime)
        : '';

      const attendData = {
        queryId: patient.id,
        doctor: hospitalData.doctorName,
        hospital: hospitalData.hospitalName,
        city: hospitalData.city,
        state: hospitalData.state,
        diagnosis: formData.diagnosis || 'Healthy',
        treatment: formData.treatment || 'Not Applicable',
        appointment: appointmentValue || 'Not Applicable',
        advice: formData.advice || 'No specific Advice',
      };

      await patientAPI.attend(patient.id, attendData);
      onRefresh();
      onClose();
    } catch (err) {
      console.error('Error marking patient as attended:', err);
      setError('Failed to mark patient as attended. Please try again.');
    }
    setLoading(false);
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal attend-modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-title">Mark Patient as Attended</h2>
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
                  {patient.age} years • {patient.contact}
                </p>
              </div>
            </div>
          </div>

          {/* Error Display */}
          {error && (
            <div className="alert alert-error">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <circle cx="12" cy="12" r="10"/>
                <line x1="15" y1="9" x2="9" y2="15"/>
                <line x1="9" y1="9" x2="15" y2="15"/>
              </svg>
              {error}
            </div>
          )}

          {/* Attend Form */}
          <div className="attend-form">
            <div className="form-section">
              <h4>Hospital Information (Compulsory)</h4>
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Doctor Name *</label>
                  <input
                    type="text"
                    name="doctorName"
                    value={hospitalData.doctorName}
                    onChange={handleDoctorChange}
                    className="form-input"
                    placeholder="Enter doctor name"
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">City *</label>
                  <input
                    type="text"
                    name="city"
                    value={hospitalData.city}
                    onChange={handleDoctorChange}
                    className="form-input"
                    placeholder="Enter city"
                  />
                </div>
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">State *</label>
                  <select
                    name="state"
                    value={hospitalData.state}
                    onChange={handleDoctorChange}
                    className="form-input form-select"
                  >
                    <option value="">Select State</option>
                    {INDIAN_STATES.map((state) => (
                      <option key={state} value={state}>
                        {state}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Hospital Name *</label>
                  <input
                    type="text"
                    name="hospitalName"
                    value={hospitalData.hospitalName}
                    onChange={handleDoctorChange}
                    className="form-input"
                    placeholder="Enter hospital name"
                  />
                </div>
              </div>
            </div>

            <div className="form-section">
              <h4>Medical Details</h4>
              <div className="form-group">
                <label className="form-label">
                  Diagnosis
                  <span className="form-optional">(if empty: Healthy)</span>
                </label>
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
                <label className="form-label">
                  Treatment
                  <span className="form-optional">(if empty: Not Applicable)</span>
                </label>
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
                <label className="form-label">
                  Advice
                  <span className="form-optional">(if empty: No specific Advice)</span>
                </label>
                <textarea
                  name="advice"
                  value={formData.advice}
                  onChange={handleChange}
                  className="form-input"
                  placeholder="Enter advice for patient"
                  rows="2"
                />
              </div>
            </div>

            <div className="form-section">
              <h4>Next Appointment</h4>
              <p className="section-description">
                Schedule a follow-up appointment (optional)
              </p>
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" style={{marginRight: '6px', verticalAlign: 'middle'}}>
                      <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                      <line x1="16" y1="2" x2="16" y2="6"/>
                      <line x1="8" y1="2" x2="8" y2="6"/>
                      <line x1="3" y1="10" x2="21" y2="10"/>
                    </svg>
                    Appointment Date
                  </label>
                  <input
                    type="date"
                    name="appointmentDate"
                    value={formData.appointmentDate}
                    onChange={handleChange}
                    className="form-input date-input"
                    min={getMinDate()}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" style={{marginRight: '6px', verticalAlign: 'middle'}}>
                      <circle cx="12" cy="12" r="10"/>
                      <polyline points="12 6 12 12 16 14"/>
                    </svg>
                    Appointment Time
                  </label>
                  <input
                    type="time"
                    name="appointmentTime"
                    value={formData.appointmentTime}
                    onChange={handleChange}
                    className="form-input time-input"
                  />
                </div>
              </div>
            </div>

            <div className="form-actions">
              <button 
                className="btn btn-secondary"
                onClick={onClose}
              >
                Cancel
              </button>
              <button 
                className="btn btn-success"
                onClick={handleAttend}
                disabled={loading}
              >
                {loading ? 'Saving...' : 'Mark as Attended'}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default AttendModal;
