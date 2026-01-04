import React, { useState, useEffect } from 'react';
import { patientAPI } from '../services/api';
import './ReceiptModal.css';

function ReceiptModal({ patient, onClose, onRefresh }) {
  const [receipt, setReceipt] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [undoLoading, setUndoLoading] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);

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

  const handleUndo = async () => {
    setUndoLoading(true);
    setError('');
    try {
      await patientAPI.unattend(patient.id);
      if (onRefresh) onRefresh();
      onClose();
    } catch (err) {
      console.error('Error undoing attendance:', err);
      setError('Failed to undo attendance. Please try again.');
      setShowConfirm(false);
    }
    setUndoLoading(false);
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

              {/* Undo Confirmation */}
              {showConfirm && (
                <div className="undo-confirm-section">
                  <div className="undo-confirm-message">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <circle cx="12" cy="12" r="10"/>
                      <line x1="12" y1="8" x2="12" y2="12"/>
                      <line x1="12" y1="16" x2="12.01" y2="16"/>
                    </svg>
                    <span>Are you sure you want to undo this attendance? The patient will be marked as unattended.</span>
                  </div>
                  <div className="undo-confirm-actions">
                    <button 
                      className="btn btn-secondary btn-sm"
                      onClick={() => setShowConfirm(false)}
                      disabled={undoLoading}
                    >
                      Cancel
                    </button>
                    <button 
                      className="btn btn-danger btn-sm"
                      onClick={handleUndo}
                      disabled={undoLoading}
                    >
                      {undoLoading ? 'Undoing...' : 'Yes, Undo Attendance'}
                    </button>
                  </div>
                </div>
              )}

              {/* Action Buttons */}
              <div className="receipt-actions">
                <button 
                  className="btn btn-secondary"
                  onClick={onClose}
                >
                  Close
                </button>
                <button 
                  className="btn btn-warning"
                  onClick={() => setShowConfirm(true)}
                  title="Undo Attendance"
                  disabled={showConfirm}
                >
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <polyline points="1 4 1 10 7 10"/>
                    <path d="M3.51 15a9 9 0 1 0 2.13-9.36L1 10"/>
                  </svg>
                  Undo
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
                  Print
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
