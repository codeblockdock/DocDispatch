import React, { useState, useMemo } from 'react';
import './DispatchSection.css';

function DispatchSection({ patients, onDispatch, onRefresh }) {
  const [sortBy, setSortBy] = useState('pincode'); // 'pincode' or 'village'
  const [selectedGroup, setSelectedGroup] = useState(null);
  const [dispatchingGroup, setDispatchingGroup] = useState(null);

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

  const handleDispatch = async (group) => {
    setDispatchingGroup(group.key);
    try {
      await onDispatch(group);
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
                        handleDispatch(group);
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
    </div>
  );
}

export default DispatchSection;
