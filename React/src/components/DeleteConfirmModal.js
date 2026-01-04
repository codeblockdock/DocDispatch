import React from 'react';
import './DeleteConfirmModal.css';

function DeleteConfirmModal({ patient, onConfirm, onCancel, loading }) {
  return (
    <div className="modal-overlay" onClick={onCancel}>
      <div className="modal delete-modal" onClick={(e) => e.stopPropagation()}>
        <div className="delete-modal-content">
          <div className="delete-icon">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="12" cy="12" r="10"/>
              <line x1="15" y1="9" x2="9" y2="15"/>
              <line x1="9" y1="9" x2="15" y2="15"/>
            </svg>
          </div>
          <h3>Delete Patient Record</h3>
          <p>
            Are you sure you want to delete the record for <strong>{patient.name}</strong>? 
            This action cannot be undone.
          </p>
          <div className="delete-modal-actions">
            <button className="btn btn-secondary" onClick={onCancel} disabled={loading}>
              Cancel
            </button>
            <button className="btn btn-danger" onClick={onConfirm} disabled={loading}>
              {loading ? 'Deleting...' : 'Delete Record'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default DeleteConfirmModal;
