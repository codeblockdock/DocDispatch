import React, { useState, useMemo, useEffect } from 'react';
import './DispatchSection.css';

// List of Indian states
const INDIAN_STATES = [
  'Andhra Pradesh', 'Arunachal Pradesh', 'Assam', 'Bihar', 'Chhattisgarh', 'Goa', 'Gujarat',
  'Haryana', 'Himachal Pradesh', 'Jharkhand', 'Karnataka', 'Kerala', 'Madhya Pradesh',
  'Maharashtra', 'Manipur', 'Meghalaya', 'Mizoram', 'Nagaland', 'Odisha', 'Punjab',
  'Rajasthan', 'Sikkim', 'Tamil Nadu', 'Telangana', 'Tripura', 'Uttar Pradesh',
  'Uttarakhand', 'West Bengal', 'Andaman and Nicobar Islands', 'Chandigarh',
  'Dadra and Nagar Haveli and Daman and Diu', 'Delhi', 'Jammu and Kashmir',
  'Ladakh', 'Lakshadweep', 'Puducherry',
];

function DispatchSection({ patients, onDispatch, onRefresh }) {
  const [sortBy, setSortBy] = useState('pincode'); // 'pincode' or 'village'
  const [selectedGroup, setSelectedGroup] = useState(null);
  const [dispatchingGroup, setDispatchingGroup] = useState(null);
  
  // Modal state
  const [showDispatchModal, setShowDispatchModal] = useState(false);
  const [pendingDispatchGroup, setPendingDispatchGroup] = useState(null);
  const [dispatchError, setDispatchError] = useState('');
  
  // Doctor details form
  const [doctorDetails, setDoctorDetails] = useState({
    doctorName: '',
    hospitalName: '',
    city: '',
    state: '',
    diagnosis: 'Field Assessment Required',
    treatment: 'Doctor Dispatched - In-person examination scheduled',
    advice: 'Doctor has been dispatched to your location. Please be available for examination.',
    appointmentDate: '',
    appointmentHour: '09',
    appointmentMinute: '00',
    appointmentPeriod: 'AM',
  });

  // Load hospital data from localStorage
  useEffect(() => {
    const storedData = localStorage.getItem('hospitalData');
    if (storedData) {
      try {
        const data = JSON.parse(storedData);
        setDoctorDetails(prev => ({
          ...prev,
          doctorName: data.doctorName || '',
          hospitalName: data.name || data.hospitalName || '',
          city: data.city || '',
          state: data.state || '',
        }));
      } catch (e) {
        console.error('Error parsing hospital data:', e);
      }
    }
  }, []);

  // Group patients by pincode or village and calculate risk factors
  const groupedPatients = useMemo(() => {
    if (!patients || patients.length === 0) return [];

    // Only include unattended patients
    const unattendedPatients = patients.filter(p => p.attended !== 1);

    const groups = {};
    unattendedPatients.forEach(patient => {
      const key = sortBy === 'pincode' 
        ? (patient.pincode || 'Unknown') 
        : (patient.village || patient.city || 'Unknown');
      
      if (!groups[key]) {
        groups[key] = {
          key,
          pincode: patient.pincode || 'Unknown',
          village: patient.village || patient.city || 'Unknown',
          city: patient.city || 'Unknown',
          state: patient.state || 'Unknown',
          patients: [],
          totalRiskFactor: 0,
        };
      }
      
      groups[key].patients.push(patient);
      groups[key].totalRiskFactor += patient.riskfactor || 1;
    });

    // Convert to array and calculate doctors needed
    return Object.values(groups).map(group => ({
      ...group,
      doctorsNeeded: Math.ceil(group.totalRiskFactor / 50),
      patientCount: group.patients.length,
    })).sort((a, b) => b.totalRiskFactor - a.totalRiskFactor);
  }, [patients, sortBy]);

  const openDispatchModal = (group) => {
    setPendingDispatchGroup(group);
    setDispatchError('');
    // Pre-fill city and state from the group
    setDoctorDetails(prev => ({
      ...prev,
      city: prev.city || group.city,
      state: prev.state || group.state,
    }));
    setShowDispatchModal(true);
  };

  const closeDispatchModal = () => {
    setShowDispatchModal(false);
    setPendingDispatchGroup(null);
    setDispatchError('');
  };

  const handleDetailChange = (e) => {
    const { name, value } = e.target;
    setDoctorDetails(prev => ({ ...prev, [name]: value }));
  };

  const handleConfirmDispatch = async () => {
    // Validation
    if (!doctorDetails.doctorName.trim()) {
      setDispatchError('Doctor name is required');
      return;
    }
    if (!doctorDetails.hospitalName.trim()) {
      setDispatchError('Hospital name is required');
      return;
    }
    if (!doctorDetails.city.trim()) {
      setDispatchError('City is required');
      return;
    }
    if (!doctorDetails.state.trim()) {
      setDispatchError('State is required');
      return;
    }
    if (!doctorDetails.appointmentDate) {
      setDispatchError('Appointment date is required');
      return;
    }

    setDispatchingGroup(pendingDispatchGroup.key);
    try {
      await onDispatch(pendingDispatchGroup, doctorDetails);
      closeDispatchModal();
    } catch (err) {
      setDispatchError('Failed to dispatch. Please try again.');
    } finally {
      setDispatchingGroup(null);
    }
  };

  const getRiskBadgeClass = (totalRisk) => {
    if (totalRisk >= 100) return 'risk-critical';
    if (totalRisk >= 50) return 'risk-high';
    if (totalRisk >= 25) return 'risk-medium';
    return 'risk-low';
  };

  if (!patients || patients.length === 0) {
    return null;
  }

  const unattendedCount = patients.filter(p => p.attended !== 1).length;
  if (unattendedCount === 0) {
    return (
      <div className="dispatch-section">
        <div className="dispatch-header">
          <div className="dispatch-title-container">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
              <polyline points="22 4 12 14.01 9 11.01"/>
            </svg>
            <h2 className="dispatch-title">Doctor Dispatch Center</h2>
          </div>
        </div>
        <div className="dispatch-empty">
          <p>All patients have been attended! No dispatch needed.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="dispatch-section">
      <div className="dispatch-header">
        <div className="dispatch-title-container">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
            <circle cx="9" cy="7" r="4"/>
            <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
            <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
          </svg>
          <h2 className="dispatch-title">Doctor Dispatch Center</h2>
        </div>
        <div className="dispatch-controls">
          <div className="sort-control">
            <label>Group by:</label>
            <select 
              value={sortBy} 
              onChange={(e) => setSortBy(e.target.value)}
              className="form-input sort-select"
            >
              <option value="pincode">Pin Code</option>
              <option value="village">Village/City</option>
            </select>
          </div>
        </div>
      </div>

      <div className="dispatch-info-bar">
        <div className="info-item">
          <span className="info-label">Total Unattended:</span>
          <span className="info-value">{unattendedCount} patients</span>
        </div>
        <div className="info-item">
          <span className="info-label">Locations:</span>
          <span className="info-value">{groupedPatients.length}</span>
        </div>
        <div className="info-item">
          <span className="info-label">Dispatch Formula:</span>
          <span className="info-value info-formula">1 Doctor per 50 Risk Factor Sum</span>
        </div>
      </div>

      <div className="dispatch-groups-container">
        <table className="dispatch-table">
          <thead>
            <tr>
              <th>Location</th>
              <th>Pin Code</th>
              <th>Patients</th>
              <th>Total Risk Factor</th>
              <th>Doctors Needed</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {groupedPatients.map((group) => (
              <React.Fragment key={group.key}>
                <tr 
                  className={`dispatch-row ${selectedGroup === group.key ? 'expanded' : ''}`}
                  onClick={() => setSelectedGroup(selectedGroup === group.key ? null : group.key)}
                >
                  <td>
                    <div className="location-cell">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
                        <circle cx="12" cy="10" r="3"/>
                      </svg>
                      <div>
                        <span className="location-name">{group.village}</span>
                        <span className="location-city">{group.city}, {group.state}</span>
                      </div>
                    </div>
                  </td>
                  <td>
                    <span className="pincode-badge">{group.pincode}</span>
                  </td>
                  <td>
                    <span className="patient-count">{group.patientCount}</span>
                  </td>
                  <td>
                    <span className={`risk-badge ${getRiskBadgeClass(group.totalRiskFactor)}`}>
                      {group.totalRiskFactor.toFixed(1)}
                    </span>
                  </td>
                  <td>
                    <div className="doctors-cell">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                        <circle cx="12" cy="7" r="4"/>
                      </svg>
                      <span className="doctors-count">{group.doctorsNeeded}</span>
                    </div>
                  </td>
                  <td>
                    <button 
                      className="btn btn-dispatch"
                      onClick={(e) => {
                        e.stopPropagation();
                        openDispatchModal(group);
                      }}
                      disabled={dispatchingGroup === group.key}
                    >
                      {dispatchingGroup === group.key ? (
                        <>
                          <div className="btn-spinner"></div>
                          Dispatching...
                        </>
                      ) : (
                        <>
                          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                            <path d="M22 2L11 13"/>
                            <path d="M22 2l-7 20-4-9-9-4 20-7z"/>
                          </svg>
                          Dispatch Doctor
                        </>
                      )}
                    </button>
                  </td>
                </tr>
                {selectedGroup === group.key && (
                  <tr className="patient-details-row">
                    <td colSpan="6">
                      <div className="patient-details-container">
                        <h4>Patients in this location ({group.patientCount})</h4>
                        <div className="patient-list">
                          {group.patients.map(patient => (
                            <div key={patient.id} className="patient-mini-card">
                              <div className="patient-mini-avatar">
                                {patient.name?.charAt(0).toUpperCase() || 'P'}
                              </div>
                              <div className="patient-mini-info">
                                <span className="patient-mini-name">{patient.name}</span>
                                <span className="patient-mini-details">
                                  {patient.age} yrs • Risk: {patient.riskfactor}
                                </span>
                              </div>
                              <span className={`badge ${
                                patient.riskfactor >= 3 ? 'badge-danger' :
                                patient.riskfactor >= 1.5 ? 'badge-orange' :
                                'badge-warning'
                              }`}>
                                {patient.riskfactor >= 3 ? 'High' :
                                 patient.riskfactor >= 1.5 ? 'Medium' : 'Low'}
                              </span>
                            </div>
                          ))}
                        </div>
                      </div>
                    </td>
                  </tr>
                )}
              </React.Fragment>
            ))}
          </tbody>
        </table>
      </div>

      {/* Dispatch Modal */}
      {showDispatchModal && pendingDispatchGroup && (
        <div className="dispatch-modal-overlay" onClick={closeDispatchModal}>
          <div className="dispatch-modal" onClick={(e) => e.stopPropagation()}>
            <div className="dispatch-modal-header">
              <h2>Dispatch Doctor</h2>
              <button className="modal-close-btn" onClick={closeDispatchModal}>
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <line x1="18" y1="6" x2="6" y2="18"/>
                  <line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
              </button>
            </div>
            
            <div className="dispatch-modal-body">
              {/* Location Summary */}
              <div className="dispatch-summary">
                <div className="summary-item">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
                    <circle cx="12" cy="10" r="3"/>
                  </svg>
                  <div>
                    <span className="summary-label">Location</span>
                    <span className="summary-value">{pendingDispatchGroup.village}, {pendingDispatchGroup.city}</span>
                  </div>
                </div>
                <div className="summary-item">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                    <circle cx="9" cy="7" r="4"/>
                  </svg>
                  <div>
                    <span className="summary-label">Patients</span>
                    <span className="summary-value">{pendingDispatchGroup.patientCount} patients</span>
                  </div>
                </div>
                <div className="summary-item">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M22 12h-4l-3 9L9 3l-3 9H2"/>
                  </svg>
                  <div>
                    <span className="summary-label">Risk Factor</span>
                    <span className="summary-value">{pendingDispatchGroup.totalRiskFactor.toFixed(1)}</span>
                  </div>
                </div>
                <div className="summary-item highlight">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                    <circle cx="12" cy="7" r="4"/>
                  </svg>
                  <div>
                    <span className="summary-label">Doctors Needed</span>
                    <span className="summary-value">{pendingDispatchGroup.doctorsNeeded}</span>
                  </div>
                </div>
              </div>

              {/* Error Display */}
              {dispatchError && (
                <div className="dispatch-error">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <circle cx="12" cy="12" r="10"/>
                    <line x1="15" y1="9" x2="9" y2="15"/>
                    <line x1="9" y1="9" x2="15" y2="15"/>
                  </svg>
                  {dispatchError}
                </div>
              )}

              {/* Doctor Details Form */}
              <div className="dispatch-form">
                <h3>Doctor & Hospital Details</h3>
                
                <div className="form-row">
                  <div className="form-group">
                    <label className="form-label">Doctor Name *</label>
                    <input
                      type="text"
                      name="doctorName"
                      value={doctorDetails.doctorName}
                      onChange={handleDetailChange}
                      className="form-input"
                      placeholder="Enter doctor name"
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Hospital Name *</label>
                    <input
                      type="text"
                      name="hospitalName"
                      value={doctorDetails.hospitalName}
                      onChange={handleDetailChange}
                      className="form-input"
                      placeholder="Enter hospital name"
                    />
                  </div>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label className="form-label">City *</label>
                    <input
                      type="text"
                      name="city"
                      value={doctorDetails.city}
                      onChange={handleDetailChange}
                      className="form-input"
                      placeholder="Enter city"
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">State *</label>
                    <select
                      name="state"
                      value={doctorDetails.state}
                      onChange={handleDetailChange}
                      className="form-input"
                    >
                      <option value="">Select State</option>
                      {INDIAN_STATES.map(state => (
                        <option key={state} value={state}>{state}</option>
                      ))}
                    </select>
                  </div>
                </div>

                <h3>Dispatch Information</h3>

                <div className="form-row">
                  <div className="form-group">
                    <label className="form-label">Appointment Date *</label>
                    <input
                      type="date"
                      name="appointmentDate"
                      value={doctorDetails.appointmentDate}
                      onChange={handleDetailChange}
                      className="form-input date-input"
                      min={new Date().toISOString().split('T')[0]}
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Appointment Time *</label>
                    <div className="time-picker-group">
                      <select
                        name="appointmentHour"
                        value={doctorDetails.appointmentHour}
                        onChange={handleDetailChange}
                        className="form-input time-select"
                      >
                        {['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'].map(h => (
                          <option key={h} value={h}>{h}</option>
                        ))}
                      </select>
                      <span className="time-separator">:</span>
                      <select
                        name="appointmentMinute"
                        value={doctorDetails.appointmentMinute}
                        onChange={handleDetailChange}
                        className="form-input time-select"
                      >
                        {['00', '15', '30', '45'].map(m => (
                          <option key={m} value={m}>{m}</option>
                        ))}
                      </select>
                      <select
                        name="appointmentPeriod"
                        value={doctorDetails.appointmentPeriod}
                        onChange={handleDetailChange}
                        className="form-input time-select period-select"
                      >
                        <option value="AM">AM</option>
                        <option value="PM">PM</option>
                      </select>
                    </div>
                  </div>
                </div>
                
                <div className="form-group">
                  <label className="form-label">Initial Diagnosis</label>
                  <input
                    type="text"
                    name="diagnosis"
                    value={doctorDetails.diagnosis}
                    onChange={handleDetailChange}
                    className="form-input"
                    placeholder="Field Assessment Required"
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Treatment Plan</label>
                  <input
                    type="text"
                    name="treatment"
                    value={doctorDetails.treatment}
                    onChange={handleDetailChange}
                    className="form-input"
                    placeholder="Doctor Dispatched - In-person examination scheduled"
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Advice to Patients</label>
                  <textarea
                    name="advice"
                    value={doctorDetails.advice}
                    onChange={handleDetailChange}
                    className="form-input form-textarea"
                    placeholder="Doctor has been dispatched to your location..."
                    rows={3}
                  />
                </div>
              </div>
            </div>

            <div className="dispatch-modal-footer">
              <button className="btn btn-secondary" onClick={closeDispatchModal}>
                Cancel
              </button>
              <button 
                className="btn btn-dispatch-confirm"
                onClick={handleConfirmDispatch}
                disabled={dispatchingGroup === pendingDispatchGroup.key}
              >
                {dispatchingGroup === pendingDispatchGroup.key ? (
                  <>
                    <div className="btn-spinner"></div>
                    Dispatching...
                  </>
                ) : (
                  <>
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M22 2L11 13"/>
                      <path d="M22 2l-7 20-4-9-9-4 20-7z"/>
                    </svg>
                    Confirm Dispatch ({pendingDispatchGroup.patientCount} patients)
                  </>
                )}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default DispatchSection;
