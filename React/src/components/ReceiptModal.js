import React, { useState, useEffect } from 'react';
import { patientAPI } from '../services/api';
import './ReceiptModal.css';

function ReceiptModal({ patient, onClose }) {
  const [receipt, setReceipt] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchReceipt = async () => {
      try {
        setLoading(true);
        setError('');
        // Fetch the full patient data which includes attended details
        const response = await patientAPI.getById(patient.id);
        setReceipt(response.data);
      } catch (err) {
        console.error('Error fetching receipt:', err);
        setError('Failed to load receipt details');
      } finally {
        setLoading(false);
      }
    };

    if (patient?.id) {
      fetchReceipt();
    }
  }, [patient]);

  const formatDateTime = (dateString) => {
    if (!dateString) return 'Not Applicable';
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString('en-IN', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      });
    } catch {
      return dateString;
    }
  };

  if (loading) {
    return (
      <div className="modal-overlay" onClick={onClose}>
        <div className="modal receipt-modal" onClick={(e) => e.stopPropagation()}>
          <div className="modal-header">
            <h2 className="modal-title">Attended Receipt</h2>
            <button className="modal-close" onClick={onClose}>
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </button>
          </div>
          <div className="modal-body">
            <div className="receipt-loading">
              <div className="spinner"></div>
              <p>Loading receipt...</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal receipt-modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-title">Attended Receipt</h2>
          <button className="modal-close" onClick={onClose}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>

        <div className="modal-body">
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

          {receipt && (
            <div className="receipt-content">
              {/* Patient Info Section */}
              <div className="receipt-section patient-section">
                <div className="patient-header">
                  <div className="patient-avatar">
                    {receipt.name?.charAt(0).toUpperCase() || 'P'}
                  </div>
                  <div className="patient-info">
                    <h3>{receipt.name}</h3>
                    <p className="patient-meta">
                      {receipt.age} years • {receipt.gender} • {receipt.contact}
                    </p>
                  </div>
                </div>
              </div>

              {/* Hospital Info Section */}
              <div className="receipt-section">
                <h4 className="section-title">Hospital Information</h4>
                <div className="receipt-grid">
                  <div className="receipt-item">
                    <span className="receipt-label">Doctor</span>
                    <span className="receipt-value">{receipt.doctor || 'N/A'}</span>
                  </div>
                  <div className="receipt-item">
                    <span className="receipt-label">Hospital</span>
                    <span className="receipt-value">{receipt.hospital || 'N/A'}</span>
                  </div>
                  <div className="receipt-item">
                    <span className="receipt-label">City</span>
                    <span className="receipt-value">{receipt.city || 'N/A'}</span>
                  </div>
                </div>
              </div>

              {/* Medical Details Section */}
              <div className="receipt-section">
                <h4 className="section-title">Medical Details</h4>
                <div className="receipt-details">
                  <div className="detail-block">
                    <span className="detail-label">Diagnosis</span>
                    <p className="detail-text">{receipt.diagnosis || 'N/A'}</p>
                  </div>
                  <div className="detail-block">
                    <span className="detail-label">Treatment</span>
                    <p className="detail-text">{receipt.treatment || 'N/A'}</p>
                  </div>
                  <div className="detail-block">
                    <span className="detail-label">Advice</span>
                    <p className="detail-text">{receipt.advice || 'N/A'}</p>
                  </div>
                </div>
              </div>

              {/* Appointment Section */}
              <div className="receipt-section">
                <h4 className="section-title">Next Appointment</h4>
                <div className="appointment-display">
                  {receipt.appointment && receipt.appointment !== 'Not Applicable' ? (
                    <>
                      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                        <line x1="16" y1="2" x2="16" y2="6"/>
                        <line x1="8" y1="2" x2="8" y2="6"/>
                        <line x1="3" y1="10" x2="21" y2="10"/>
                      </svg>
                      <span>{formatDateTime(receipt.appointment)}</span>
                    </>
                  ) : (
                    <span className="not-applicable">Not Applicable</span>
                  )}
                </div>
              </div>

              {/* Attended Date */}
              <div className="receipt-section attended-date-section">
                <span className="attended-label">Attended on</span>
                <span className="attended-value">{receipt.attendedTimestamp || formatDateTime(receipt.receivedAt)}</span>
              </div>

              {/* Print Button */}
              <div className="receipt-actions">
                <button 
                  className="btn btn-secondary"
                  onClick={onClose}
                >
                  Close
                </button>
                <button 
                  className="btn btn-primary"
                  onClick={() => window.print()}
                >
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <polyline points="6 9 6 2 18 2 18 9"/>
                    <path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"/>
                    <rect x="6" y="14" width="12" height="8"/>
                  </svg>
                  Print Receipt
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default ReceiptModal;
