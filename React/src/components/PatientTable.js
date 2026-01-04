import React from 'react';
import './PatientTable.css';

function PatientTable({ patients, loading, onView, onAttend, onViewReceipt }) {
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
            <th>Age</th>
            <th>Risk Factor</th>
            <th>Symptoms</th>
            <th>City</th>
            <th>State</th>
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
                <span className="age-cell">{patient.age} yrs</span>
              </td>
              <td>
                <span className={`badge ${getRiskBadge(patient.riskfactor, patient.age)}`}>
                  {getRiskLabel(patient.riskfactor, patient.age)}
                </span>
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
              <td>{patient.city}</td>
              <td>{patient.state}</td>
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
                  {patient.attended === 1 ? (
                    <button 
                      className="btn btn-sm btn-success"
                      onClick={() => onViewReceipt(patient)}
                      title="View Receipt"
                    >
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                        <polyline points="14 2 14 8 20 8"/>
                        <line x1="12" y1="19" x2="12" y2="11"/>
                        <line x1="9" y1="16" x2="15" y2="16"/>
                      </svg>
                      View Receipt
                    </button>
                  ) : (
                    <button 
                      className="btn btn-sm btn-primary"
                      onClick={() => onAttend(patient)}
                      title="Mark as Attended"
                    >
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M20 6L9 17l-5-5"/>
                      </svg>
                      Attend
                    </button>
                  )}
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
