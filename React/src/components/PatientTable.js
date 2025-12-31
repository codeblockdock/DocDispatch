import React from 'react';
import './PatientTable.css';

function PatientTable({ patients, loading, onView, onDelete }) {
  const getStatusBadge = (status) => {
    const statusMap = {
      'High Risk': 'badge-danger',
      'Medium Risk': 'badge-warning',
      'Attended': 'badge-success',
      'Pending': 'badge-secondary',
    };
    return statusMap[status] || 'badge-secondary';
  };

  const getProbabilityColor = (probability) => {
    if (probability >= 70) return 'prob-high';
    if (probability >= 40) return 'prob-medium';
    return 'prob-low';
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

  if (loading) {
    return (
      <div className="table-container">
        <div className="table-loading">
          <div className="spinner"></div>
          <p>Loading patients...</p>
        </div>
      </div>
    );
  }

  if (!patients || patients.length === 0) {
    return (
      <div className="table-container">
        <div className="table-empty">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
            <circle cx="9" cy="7" r="4"/>
            <line x1="23" y1="11" x2="17" y2="11"/>
          </svg>
          <h3>No patients found</h3>
          <p>There are no patient records matching your criteria in this state.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="table-container">
      <table className="table">
        <thead>
          <tr>
            <th>Patient Name</th>
            <th>Symptoms</th>
            <th>Predicted Disease</th>
            <th>Probability</th>
            <th>City</th>
            <th>State</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {patients.map((patient) => (
            <tr key={patient.id}>
              <td>
                <div className="patient-name-cell">
                  <div className="patient-avatar">
                    {patient.name?.charAt(0).toUpperCase() || 'P'}
                  </div>
                  <div>
                    <span className="patient-name">{patient.name}</span>
                    <span className="patient-contact">{patient.contact}</span>
                  </div>
                </div>
              </td>
              <td>
                <div className="symptoms-cell">
                  {parseSymptoms(patient.symptoms).slice(0, 3).map((symptom, i) => (
                    <span key={i} className="symptom-tag">{symptom}</span>
                  ))}
                  {parseSymptoms(patient.symptoms).length > 3 && (
                    <span className="symptom-more">
                      +{parseSymptoms(patient.symptoms).length - 3} more
                    </span>
                  )}
                </div>
              </td>
              <td>
                <span className="disease-name">
                  {patient.predictedDisease || 'Not predicted'}
                </span>
              </td>
              <td>
                <div className={`probability ${getProbabilityColor(patient.probability)}`}>
                  <div className="probability-bar">
                    <div 
                      className="probability-fill" 
                      style={{ width: `${patient.probability}%` }}
                    ></div>
                  </div>
                  <span>{patient.probability}%</span>
                </div>
              </td>
              <td>{patient.city}</td>
              <td>{patient.state}</td>
              <td>
                <span className={`badge ${getStatusBadge(patient.status)}`}>
                  {patient.status}
                </span>
              </td>
              <td>
                <div className="actions-cell">
                  <button 
                    className="btn btn-sm btn-secondary"
                    onClick={() => onView(patient)}
                    title="View Details"
                  >
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                      <circle cx="12" cy="12" r="3"/>
                    </svg>
                    View
                  </button>
                  <button 
                    className="btn btn-sm btn-danger"
                    onClick={() => onDelete(patient)}
                    title="Delete"
                  >
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <polyline points="3 6 5 6 21 6"/>
                      <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <div className="table-footer">
        <p>Showing {patients.length} patient{patients.length !== 1 ? 's' : ''}</p>
      </div>
    </div>
  );
}

export default PatientTable;
