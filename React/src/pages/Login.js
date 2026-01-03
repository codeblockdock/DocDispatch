import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Login.css';

function Login() {
  const [formData, setFormData] = useState({
    hospitalId: '',
    password: '',
  });
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setError('');
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    console.log('Attempting login with ID:', formData.hospitalId);
    const result = await login(formData.hospitalId, formData.password);
    console.log('Login result:', result);
    
    if (result.success) {
      navigate('/dashboard');
    } else {
      const errorMsg = result.message || 'Login failed. Please check your credentials.';
      console.error('Login error:', errorMsg);
      setError(errorMsg);
    }
    
    setLoading(false);
  };

  return (
    <div className='login-container'>
      <div className='login-background'>
        <div className='login-bg-pattern'></div>
      </div>
      
      <div className='login-card'>
        <div className='login-header'>
          <div className='login-logo'>
            <svg viewBox='0 0 24 24' fill='none' xmlns='http://www.w3.org/2000/svg'>
              <path d='M12 2L2 7L12 12L22 7L12 2Z' stroke='currentColor' strokeWidth='2' strokeLinecap='round' strokeLinejoin='round'/>
              <path d='M2 17L12 22L22 17' stroke='currentColor' strokeWidth='2' strokeLinecap='round' strokeLinejoin='round'/>
              <path d='M2 12L12 17L22 12' stroke='currentColor' strokeWidth='2' strokeLinecap='round' strokeLinejoin='round'/>
            </svg>
          </div>
          <h1 className='login-title'>Hospital Data Portal</h1>
          <p className='login-subtitle'>
            Sign in to access patient records
          </p>
        </div>

        <div className='login-tabs'>
          <button 
            className='login-tab active'
          >
            Login
          </button>
          <button 
            className='login-tab'
            onClick={() => navigate('/register')}
          >
            Register
          </button>
        </div>

        {error && (
          <div className='alert alert-error'>
            <svg width='20' height='20' viewBox='0 0 24 24' fill='none' stroke='currentColor' strokeWidth='2'>
              <circle cx='12' cy='12' r='10'/>
              <line x1='15' y1='9' x2='9' y2='15'/>
              <line x1='9' y1='9' x2='15' y2='15'/>
            </svg>
            {error}
          </div>
        )}

        <form onSubmit={handleLogin} className='login-form'>
          <div className='form-group'>
            <label className='form-label'>Hospital ID</label>
            <input
              type='text'
              name='hospitalId'
              value={formData.hospitalId}
              onChange={handleChange}
              className='form-input'
              placeholder='Enter your Hospital ID'
              required
            />
          </div>

          <div className='form-group'>
            <label className='form-label'>Password</label>
            <div style={{ position: 'relative' }}>
              <input
                type={showPassword ? 'text' : 'password'}
                name='password'
                value={formData.password}
                onChange={handleChange}
                className='form-input'
                placeholder='Enter your password'
                required
              />
              <button
                type='button'
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

          <button type='submit' className='btn btn-primary login-btn' disabled={loading}>
            {loading ? (
              <span className='btn-loading'>
                <span className='spinner-small'></span>
                Signing in...
              </span>
            ) : (
              'Sign In'
            )}
          </button>
        </form>

        <div className='login-footer'>
          <p>
            Don't have an account?{' '}
            <button 
              type='button'
              className='login-link'
              onClick={() => navigate('/register')}
            >
              Register here
            </button>
          </p>
        </div>
      </div>
    </div>
  );
}

export default Login;
