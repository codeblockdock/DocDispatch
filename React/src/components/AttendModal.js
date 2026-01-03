import React, { useState, useEffect } from 'react';
import { patientAPI } from '../services/api';
import './AttendModal.css';

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
    appointment: '',
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
    
    // Parse date in dd/mm/yyyy format
    const [day, month, year] = dateStr.split('/');
    
    // Parse time in HH:MM AM/PM format
    let [hours, minutes] = timeStr.split(':');
    const meridiem = timeStr.includes('AM') ? 'AM' : 'PM';
    
    hours = parseInt(hours);
    minutes = parseInt(minutes);
    
    // Convert to 24-hour format
    if (meridiem === 'PM' && hours !== 12) {
      hours += 12;
    } else if (meridiem === 'AM' && hours === 12) {
      hours = 0;
    }
    
    // Pad with zeros
    const paddedHours = hours.toString().padStart(2, '0');
    const paddedMinutes = minutes.toString().padStart(2, '0');
    
    // Create ISO string
    return `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}T${paddedHours}:${paddedMinutes}:00`;
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
      const appointmentValue = formData.appointment && formData.appointmentTime 
        ? formatDateTimeToISO(formData.appointment, formData.appointmentTime)
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
                  <input
                    type="text"
                    name="state"
                    value={hospitalData.state}
                    onChange={handleDoctorChange}
                    className="form-input"
                    placeholder="Enter state"
                  />
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
              <h4>Appointment</h4>
              <p className="section-description">
                if empty: Not Applicable
              </p>
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">
                    Appointment Date
                    <span className="form-optional">(dd/mm/yyyy)</span>
                  </label>
                  <input
                    type="text"
                    name="appointment"
                    value={formData.appointment}
                    onChange={handleChange}
                    className="form-input"
                    placeholder="dd/mm/yyyy"
                    pattern="\d{2}/\d{2}/\d{4}"
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">
                    Time
                    <span className="form-optional">(hh:mm AM/PM)</span>
                  </label>
                  <input
                    type="text"
                    name="appointmentTime"
                    value={formData.appointmentTime}
                    onChange={handleChange}
                    className="form-input"
                    placeholder="hh:mm AM/PM"
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
