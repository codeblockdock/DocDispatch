import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import emailjs from '@emailjs/browser';
import './Register.css';

const INDIAN_STATES = [
  'Andhra Pradesh', 'Arunachal Pradesh', 'Assam', 'Bihar', 'Chhattisgarh',
  'Goa', 'Gujarat', 'Haryana', 'Himachal Pradesh', 'Jharkhand', 'Karnataka',
  'Kerala', 'Madhya Pradesh', 'Maharashtra', 'Manipur', 'Meghalaya', 'Mizoram',
  'Nagaland', 'Odisha', 'Punjab', 'Rajasthan', 'Sikkim', 'Tamil Nadu',
  'Telangana', 'Tripura', 'Uttar Pradesh', 'Uttarakhand', 'West Bengal',
  'Andaman and Nicobar Islands', 'Chandigarh', 'Dadra and Nagar Haveli and Daman and Diu',
  'Delhi', 'Jammu and Kashmir', 'Ladakh', 'Lakshadweep', 'Puducherry'
];

// Initialize EmailJS
emailjs.init(process.env.REACT_APP_EMAILJS_PUBLIC_KEY);

function Register() {
  const navigate = useNavigate();
  const [selectedOption, setSelectedOption] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [showToken, setShowToken] = useState(false);

  // ========== OPTION 1: Partner Registration ==========
  const [partnerForm, setPartnerForm] = useState({
    hospitalName: '',
    city: '',
    state: '',
    email: '',
  });
  const [partnerSuccess, setPartnerSuccess] = useState(false);

  // ========== OPTION 2: Existing Hospital Registration ==========
  const [existingForm, setExistingForm] = useState({
    hospitalId: '',
    token: '',
    password: '',
    confirmPassword: '',
  });
  const [tokenVerified, setTokenVerified] = useState(false);
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [hospitalDetails, setHospitalDetails] = useState(null);

  // ========== OPTION 1: Partner Registration Handlers ==========
  const handlePartnerChange = (e) => {
    setPartnerForm({ ...partnerForm, [e.target.name]: e.target.value });
  };

  const validatePartnerForm = () => {
    if (!partnerForm.hospitalName.trim()) {
      setMessage({ type: 'error', text: 'Hospital Name is required' });
      return false;
    }
    if (!partnerForm.city.trim()) {
      setMessage({ type: 'error', text: 'City is required' });
      return false;
    }
    if (!partnerForm.state) {
      setMessage({ type: 'error', text: 'State is required' });
      return false;
    }
    if (!partnerForm.email.trim() || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(partnerForm.email)) {
      setMessage({ type: 'error', text: 'Valid email address is required' });
      return false;
    }
    return true;
  };

  const handlePartnerSubmit = async (e) => {
    e.preventDefault();
    setMessage({ type: '', text: '' });

    if (!validatePartnerForm()) {
      return;
    }

    setLoading(true);

    try {
      // Send email to admin
      const adminResponse = await emailjs.send(
        process.env.REACT_APP_EMAILJS_SERVICE_ID,
        process.env.REACT_APP_EMAILJS_TEMPLATE_ID,
        {
          hospital_name: partnerForm.hospitalName,
          city: partnerForm.city,
          state: partnerForm.state,
          email: partnerForm.email,
          admin_email: process.env.REACT_APP_EMAILJS_ADMIN_EMAIL,
        }
      );

      setPartnerSuccess(true);
      setMessage({ 
        type: 'success', 
        text: 'Request sent successfully! You will be contacted by us shortly.' 
      });

      // Reset form
      setPartnerForm({
        hospitalName: '',
        city: '',
        state: '',
        email: '',
      });
    } catch (error) {
      console.error('EmailJS Error:', error);
      setMessage({ 
        type: 'error', 
        text: `Failed to send request: ${error.text || error.message || JSON.stringify(error)}` 
      });
    }

    setLoading(false);
  };

  // ========== OPTION 2: Existing Hospital Handlers ==========
  const handleExistingChange = (e) => {
    setExistingForm({ ...existingForm, [e.target.name]: e.target.value });
  };

  const handleVerifyToken = async (e) => {
    e.preventDefault();
    setMessage({ type: '', text: '' });

    if (!existingForm.hospitalId.trim()) {
      setMessage({ type: 'error', text: 'Hospital ID is required' });
      return;
    }

    if (!existingForm.token.trim()) {
      setMessage({ type: 'error', text: 'Token is required' });
      return;
    }

    setLoading(true);

    try {
      // API call to verify token and get hospital details
      const apiUrl = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8081';
      const response = await fetch(`${apiUrl}/hospital/verify-token`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          hospitalId: existingForm.hospitalId,
          token: existingForm.token,
        }),
      });

      const data = await response.json();

      if (data.success) {
        setTokenVerified(true);
        setHospitalDetails(data.hospital);
        setMessage({ type: 'success', text: 'Token verified successfully!' });
      } else {
        setMessage({ type: 'error', text: data.message || 'Invalid Hospital ID or Token' });
      }
    } catch (error) {
      console.error('Verification Error:', error);
      setMessage({ type: 'error', text: `Verification failed: ${error.message || JSON.stringify(error)}` });
    }

    setLoading(false);
  };

  const handlePasswordValidation = () => {
    if (!existingForm.password.trim()) {
      setMessage({ type: 'error', text: 'Password is required' });
      return false;
    }
    if (existingForm.password.length < 6) {
      setMessage({ type: 'error', text: 'Password must be at least 6 characters' });
      return false;
    }
    if (existingForm.password !== existingForm.confirmPassword) {
      setMessage({ type: 'error', text: 'Passwords do not match' });
      return false;
    }
    return true;
  };

  const handleProceedRegistration = async (e) => {
    e.preventDefault();
    setMessage({ type: '', text: '' });

    if (!handlePasswordValidation()) {
      return;
    }

    // Show confirmation page instead of directly registering
    setShowConfirmation(true);
  };

  const handleConfirmRegistration = async () => {
    setMessage({ type: '', text: '' });
    setLoading(true);

    try {
      // API call to confirm and save password, delete token
      const apiUrl = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8081';
      const response = await fetch(`${apiUrl}/hospital/confirm-registration`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          hospitalId: existingForm.hospitalId,
          token: existingForm.token,
          password: existingForm.password,
        }),
      });

      const data = await response.json();

      if (data.success) {
        setMessage({ type: 'success', text: 'Registration completed successfully! You can now login.' });
        setTimeout(() => {
          navigate('/login');
        }, 2000);
      } else {
        setMessage({ type: 'error', text: data.message || 'Registration failed' });
        setShowConfirmation(false);
      }
    } catch (error) {
      console.error('Registration Error:', error);
      setMessage({ type: 'error', text: `Registration failed: ${error.message || JSON.stringify(error)}` });
      setShowConfirmation(false);
    }

    setLoading(false);
  };

  const handleRejectRegistration = () => {
    // Cancel confirmation and go back to password form
    setShowConfirmation(false);
    setMessage({ type: '', text: '' });
  };

  const handleCancel = () => {
    setTokenVerified(false);
    setShowConfirmation(false);
    setHospitalDetails(null);
    setExistingForm({
      hospitalId: '',
      token: '',
      password: '',
      confirmPassword: '',
    });
    setMessage({ type: '', text: '' });
  };

  return (
    <div className="register-container">
      <div className="register-background">
        <div className="register-bg-pattern"></div>
      </div>

      <div className="register-card">
        <h1 className="register-title">Hospital Registration</h1>

        {/* Option Selection */}
        {selectedOption === null && (
          <div className="option-selector">
            <p className="option-title">Choose your registration type:</p>
            <div className="option-buttons">
              <button
                className="option-btn option-1"
                onClick={() => {
                  setSelectedOption('partner');
                  setMessage({ type: '', text: '' });
                }}
              >
                <div className="option-icon">🤝</div>
                <div className="option-text">
                  <strong>Partner With Us</strong>
                  <small>New Hospital Registration</small>
                </div>
              </button>

              <button
                className="option-btn option-2"
                onClick={() => {
                  setSelectedOption('existing');
                  setMessage({ type: '', text: '' });
                }}
              >
                <div className="option-icon">🔑</div>
                <div className="option-text">
                  <strong>Existing Hospital</strong>
                  <small>Have a Token? Register here</small>
                </div>
              </button>
            </div>
          </div>
        )}

        {/* OPTION 1: Partner Registration Form */}
        {selectedOption === 'partner' && (
          <form onSubmit={handlePartnerSubmit} className="register-form">
            <button
              type="button"
              className="back-btn"
              onClick={() => {
                setSelectedOption(null);
                setPartnerSuccess(false);
                setMessage({ type: '', text: '' });
              }}
            >
              ← Back
            </button>

            <h2>Partner With Us</h2>
            <p className="form-subtitle">Fill your details to partner with DocDispatch</p>

            {!partnerSuccess ? (
              <>
                <div className="form-group">
                  <label htmlFor="hospitalName">Hospital Name *</label>
                  <input
                    type="text"
                    id="hospitalName"
                    name="hospitalName"
                    value={partnerForm.hospitalName}
                    onChange={handlePartnerChange}
                    placeholder="Enter hospital name"
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="city">City *</label>
                  <input
                    type="text"
                    id="city"
                    name="city"
                    value={partnerForm.city}
                    onChange={handlePartnerChange}
                    placeholder="Enter city"
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="state">State *</label>
                  <select
                    id="state"
                    name="state"
                    value={partnerForm.state}
                    onChange={handlePartnerChange}
                    required
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
                  <label htmlFor="email">Email Address *</label>
                  <input
                    type="email"
                    id="email"
                    name="email"
                    value={partnerForm.email}
                    onChange={handlePartnerChange}
                    placeholder="Enter email address"
                    required
                  />
                </div>

                {message.text && (
                  <div className={`message ${message.type}`}>
                    {message.text}
                  </div>
                )}

                <button type="submit" className="submit-btn" disabled={loading}>
                  {loading ? 'Sending...' : 'Send Request'}
                </button>
              </>
            ) : (
              <div className="success-message">
                <div className="success-icon">✓</div>
                <h3>Thank You!</h3>
                <p>Your partnership request has been sent successfully.</p>
                <p className="success-note">Our team will contact you shortly at <strong>{partnerForm.email}</strong></p>
                <button
                  type="button"
                  className="back-home-btn"
                  onClick={() => {
                    setSelectedOption(null);
                    setPartnerSuccess(false);
                  }}
                >
                  Back to Options
                </button>
              </div>
            )}
          </form>
        )}

        {/* OPTION 2: Existing Hospital Registration Form */}
        {selectedOption === 'existing' && (
          <form onSubmit={tokenVerified ? handleProceedRegistration : handleVerifyToken} className="register-form">
            <button
              type="button"
              className="back-btn"
              onClick={() => {
                setSelectedOption(null);
                setTokenVerified(false);
                setHospitalDetails(null);
                setExistingForm({
                  hospitalId: '',
                  token: '',
                  password: '',
                  confirmPassword: '',
                });
                setMessage({ type: '', text: '' });
              }}
            >
              ← Back
            </button>

            <h2>Hospital Registration</h2>

            {!tokenVerified ? (
              <>
                <p className="form-subtitle">Enter your Hospital ID and Token to proceed</p>

                <div className="form-group">
                  <label htmlFor="hospitalId">Hospital ID *</label>
                  <input
                    type="text"
                    id="hospitalId"
                    name="hospitalId"
                    value={existingForm.hospitalId}
                    onChange={handleExistingChange}
                    placeholder="Enter Hospital ID"
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="token">Token *</label>
                  <div style={{ position: 'relative' }}>
                    <input
                      type={showToken ? 'text' : 'password'}
                      id="token"
                      name="token"
                      value={existingForm.token}
                      onChange={handleExistingChange}
                      placeholder="Enter Token"
                      required
                    />
                    <button
                      type="button"
                      onClick={() => setShowToken(!showToken)}
                      style={{
                        position: 'absolute',
                        right: '12px',
                        top: '50%',
                        transform: 'translateY(-50%)',
                        background: 'none',
                        border: 'none',
                        cursor: 'pointer',
                        color: '#666',
                        padding: '4px',
                      }}
                    >
                      {showToken ? (
                        <svg width='20' height='20' viewBox='0 0 24 24' fill='none' stroke='currentColor' strokeWidth='2'>
                          <path d='M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z'/>
                          <circle cx='12' cy='12' r='3'/>
                        </svg>
                      ) : (
                        <svg width='20' height='20' viewBox='0 0 24 24' fill='none' stroke='currentColor' strokeWidth='2'>
                          <path d='M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24'/>
                          <line x1='1' y1='1' x2='23' y2='23'/>
                        </svg>
                      )}
                    </button>
                  </div>
                </div>

                {message.text && (
                  <div className={`message ${message.type}`}>
                    {message.text}
                  </div>
                )}

                <button type="submit" className="submit-btn" disabled={loading}>
                  {loading ? 'Verifying...' : 'Verify'}
                </button>
              </>
            ) : !showConfirmation ? (
              <>
                <p className="form-subtitle">Set your password to complete registration</p>

                {hospitalDetails && (
                  <div className="hospital-details">
                    <h3>Hospital Details</h3>
                    <div className="detail-row">
                      <span className="detail-label">ID:</span>
                      <span className="detail-value">{hospitalDetails.id}</span>
                    </div>
                    <div className="detail-row">
                      <span className="detail-label">Name:</span>
                      <span className="detail-value">{hospitalDetails.name}</span>
                    </div>
                    <div className="detail-row">
                      <span className="detail-label">City:</span>
                      <span className="detail-value">{hospitalDetails.city}</span>
                    </div>
                  </div>
                )}

                <div className="form-group">
                  <label htmlFor="password">Password *</label>
                  <div style={{ position: 'relative' }}>
                    <input
                      type={showPassword ? 'text' : 'password'}
                      id="password"
                      name="password"
                      value={existingForm.password}
                      onChange={handleExistingChange}
                      placeholder="Enter password"
                      required
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      style={{
                        position: 'absolute',
                        right: '12px',
                        top: '50%',
                        transform: 'translateY(-50%)',
                        background: 'none',
                        border: 'none',
                        cursor: 'pointer',
                        color: '#666',
                        padding: '4px',
                      }}
                    >
                      {showPassword ? (
                        <svg width='20' height='20' viewBox='0 0 24 24' fill='none' stroke='currentColor' strokeWidth='2'>
                          <path d='M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z'/>
                          <circle cx='12' cy='12' r='3'/>
                        </svg>
                      ) : (
                        <svg width='20' height='20' viewBox='0 0 24 24' fill='none' stroke='currentColor' strokeWidth='2'>
                          <path d='M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24'/>
                          <line x1='1' y1='1' x2='23' y2='23'/>
                        </svg>
                      )}
                    </button>
                  </div>
                </div>

                <div className="form-group">
                  <label htmlFor="confirmPassword">Confirm Password *</label>
                  <div style={{ position: 'relative' }}>
                    <input
                      type={showConfirmPassword ? 'text' : 'password'}
                      id="confirmPassword"
                      name="confirmPassword"
                      value={existingForm.confirmPassword}
                      onChange={handleExistingChange}
                      placeholder="Confirm password"
                      required
                    />
                    <button
                      type="button"
                      onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                      style={{
                        position: 'absolute',
                        right: '12px',
                        top: '50%',
                        transform: 'translateY(-50%)',
                        background: 'none',
                        border: 'none',
                        cursor: 'pointer',
                        color: '#666',
                        padding: '4px',
                      }}
                    >
                      {showConfirmPassword ? (
                        <svg width='20' height='20' viewBox='0 0 24 24' fill='none' stroke='currentColor' strokeWidth='2'>
                          <path d='M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z'/>
                          <circle cx='12' cy='12' r='3'/>
                        </svg>
                      ) : (
                        <svg width='20' height='20' viewBox='0 0 24 24' fill='none' stroke='currentColor' strokeWidth='2'>
                          <path d='M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24'/>
                          <line x1='1' y1='1' x2='23' y2='23'/>
                        </svg>
                      )}
                    </button>
                  </div>
                </div>

                {message.text && (
                  <div className={`message ${message.type}`}>
                    {message.text}
                  </div>
                )}

                <div className="button-group">
                  <button type="submit" className="submit-btn" disabled={loading}>
                    {loading ? 'Preparing...' : 'Proceed'}
                  </button>
                  <button type="button" className="cancel-btn" onClick={handleCancel}>
                    Cancel
                  </button>
                </div>
              </>
            ) : (
              <>
                <div className="confirmation-section">
                  <div className="confirmation-icon">⚠️</div>
                  <h3>Confirm Registration</h3>
                  <p className="confirmation-message">
                    Please review your details before confirming. Once you click confirm, your password will be saved and your token will be deleted.
                  </p>
                  
                  {hospitalDetails && (
                    <div className="hospital-details">
                      <h4>Hospital Details</h4>
                      <div className="detail-row">
                        <span className="detail-label">ID:</span>
                        <span className="detail-value">{hospitalDetails.id}</span>
                      </div>
                      <div className="detail-row">
                        <span className="detail-label">Name:</span>
                        <span className="detail-value">{hospitalDetails.name}</span>
                      </div>
                      <div className="detail-row">
                        <span className="detail-label">City:</span>
                        <span className="detail-value">{hospitalDetails.city}</span>
                      </div>
                    </div>
                  )}
                  
                  <div className="warning-box">
                    <p><strong>⚠️ Important:</strong> This action cannot be undone. Your token will be permanently deleted after confirmation.</p>
                  </div>

                  {message.text && (
                    <div className={`message ${message.type}`}>
                      {message.text}
                    </div>
                  )}
                </div>

                <div className="button-group">
                  <button 
                    type="button" 
                    className="submit-btn" 
                    onClick={handleConfirmRegistration}
                    disabled={loading}
                  >
                    {loading ? 'Confirming...' : 'Yes, Confirm Registration'}
                  </button>
                  <button 
                    type="button" 
                    className="cancel-btn" 
                    onClick={handleRejectRegistration}
                    disabled={loading}
                  >
                    No, Cancel
                  </button>
                </div>
              </>
            )}
          </form>
        )}
        
        <div className="register-footer">
          <p>
            Already have an account?{' '}
            <button 
              type="button" 
              className="login-link"
              onClick={() => navigate('/login')}
              style={{
                background: 'none',
                border: 'none',
                color: 'var(--primary-color)',
                fontWeight: '600',
                cursor: 'pointer',
                textDecoration: 'underline'
              }}
            >
              Back to Login
            </button>
          </p>
        </div>
      </div>
    </div>
  );
}

export default Register;
