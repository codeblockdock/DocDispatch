import React, { useState } from 'react';
import './PatientTable.css';

const ITEMS_PER_PAGE = 10;

function PatientTable({ patients, loading, onView, onAttend, onViewReceipt, onDelete, isAdmin }) {
  const [currentPage, setCurrentPage] = useState(1);
  
  const getRiskBadge = (risk, age) => {
    if ((age >= 5 && age <= 12) || (age >= 51 && age <= 60)) return 'badge-purple';
    if (risk === 3.0) return 'badge-danger';
    if (risk === 2.0) return 'badge-orange';
    if (risk === 1.0) return 'badge-warning';
    return 'badge-secondary';
  };

  const getRiskLabel = (risk, age) => {
    let labels = [];
    if (risk === 3.0) labels.push('High');
    else if (risk === 2.0) labels.push('Medium');
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

  // Pagination calculations
  const totalPages = Math.ceil(patients.length / ITEMS_PER_PAGE);
  const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
  const endIndex = startIndex + ITEMS_PER_PAGE;
  const currentPatients = patients.slice(startIndex, endIndex);

  const handlePageChange = (page) => {
    if (page >= 1 && page <= totalPages) {
      setCurrentPage(page);
    }
  };

  const getPageNumbers = () => {
    const pages = [];
    const maxVisiblePages = 5;
    
    if (totalPages <= maxVisiblePages) {
      for (let i = 1; i <= totalPages; i++) {
        pages.push(i);
      }
    } else {
      if (currentPage <= 3) {
        for (let i = 1; i <= 4; i++) pages.push(i);
        pages.push('...');
        pages.push(totalPages);
      } else if (currentPage >= totalPages - 2) {
        pages.push(1);
        pages.push('...');
        for (let i = totalPages - 3; i <= totalPages; i++) pages.push(i);
      } else {
        pages.push(1);
        pages.push('...');
        for (let i = currentPage - 1; i <= currentPage + 1; i++) pages.push(i);
        pages.push('...');
        pages.push(totalPages);
      }
    }
    return pages;
  };

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
          {currentPatients.map((patient) => (
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
                  {isAdmin && (
                    <button 
                      className="btn btn-sm btn-danger"
                      onClick={() => onDelete(patient)}
                      title="Delete Patient"
                    >
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <polyline points="3 6 5 6 21 6"/>
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                      </svg>
                    </button>
                  )}
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <div className="table-footer">
        <p>Showing {startIndex + 1}-{Math.min(endIndex, patients.length)} of {patients.length} patient{patients.length !== 1 ? 's' : ''}</p>
        
        {totalPages > 1 && (
          <div className="pagination">
            <button 
              className="pagination-btn"
              onClick={() => handlePageChange(currentPage - 1)}
              disabled={currentPage === 1}
            >
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <polyline points="15 18 9 12 15 6"/>
              </svg>
            </button>
            
            {getPageNumbers().map((page, index) => (
              page === '...' ? (
                <span key={`ellipsis-${index}`} className="pagination-ellipsis">...</span>
              ) : (
                <button
                  key={page}
                  className={`pagination-btn ${currentPage === page ? 'active' : ''}`}
                  onClick={() => handlePageChange(page)}
                >
                  {page}
                </button>
              )
            ))}
            
            <button 
              className="pagination-btn"
              onClick={() => handlePageChange(currentPage + 1)}
              disabled={currentPage === totalPages}
            >
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <polyline points="9 18 15 12 9 6"/>
              </svg>
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

export default PatientTable;
