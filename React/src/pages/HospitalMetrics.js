import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { statsAPI } from '../services/api';
import Header from '../components/Header';
import HospitalStatsTable from '../components/HospitalStatsTable';
import './HospitalMetrics.css';

const INDIAN_STATES = [
  'Andhra Pradesh', 'Arunachal Pradesh', 'Assam', 'Bihar', 'Chhattisgarh', 'Goa', 'Gujarat',
  'Haryana', 'Himachal Pradesh', 'Jharkhand', 'Karnataka', 'Kerala', 'Madhya Pradesh',
  'Maharashtra', 'Manipur', 'Meghalaya', 'Mizoram', 'Nagaland', 'Odisha', 'Punjab',
  'Rajasthan', 'Sikkim', 'Tamil Nadu', 'Telangana', 'Tripura', 'Uttar Pradesh',
  'Uttarakhand', 'West Bengal', 'Andaman and Nicobar Islands', 'Chandigarh',
  'Dadra and Nagar Haveli and Daman and Diu', 'Delhi', 'Jammu and Kashmir',
  'Ladakh', 'Lakshadweep', 'Puducherry',
];

function HospitalMetrics() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [allStats, setAllStats] = useState([]);
  const [filteredStats, setFilteredStats] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  const [filters, setFilters] = useState({
    state: '',
    city: '',
  });

  const fetchStats = useCallback(async () => {
    try {
      setLoading(true);
      const response = await statsAPI.getAllHospitalStats();
      setAllStats(response.data);
      setFilteredStats(response.data);
    } catch (err) {
      console.error('Error fetching hospital stats:', err);
      setError('Failed to load hospital metrics.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (!user?.isAdmin) {
      navigate('/dashboard');
      return;
    }
    fetchStats();
  }, [user, navigate, fetchStats]);

  useEffect(() => {
    let result = allStats;
    
    if (filters.state) {
      result = result.filter(h => h.state === filters.state);
    }
    
    if (filters.city) {
      result = result.filter(h => 
        h.city?.toLowerCase().includes(filters.city.toLowerCase())
      );
    }
    
    setFilteredStats(result);
  }, [filters, allStats]);

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({ ...prev, [name]: value }));
  };

  return (
    <div className="hospital-metrics-page">
      <Header />
      
      <main className="metrics-main">
        <div className="container">
          <div className="metrics-header">
            <div>
              <h1 className="metrics-title">Hospital Performance Analytics</h1>
              <p className="metrics-subtitle">Monitor and manage hospital service delivery across regions</p>
            </div>
            <button className="btn btn-secondary" onClick={() => navigate('/dashboard')}>
              Back to Dashboard
            </button>
          </div>

          <div className="metrics-filters-card">
            <div className="metrics-filters-grid">
              <div className="filter-group">
                <label>Select State</label>
                <select 
                  name="state" 
                  value={filters.state} 
                  onChange={handleFilterChange}
                  className="form-input"
                >
                  <option value="">All States</option>
                  {INDIAN_STATES.map(state => (
                    <option key={state} value={state}>{state}</option>
                  ))}
                </select>
              </div>
              <div className="filter-group">
                <label>Filter by City</label>
                <input
                  type="text"
                  name="city"
                  value={filters.city}
                  onChange={handleFilterChange}
                  className="form-input"
                  placeholder="Type city name..."
                />
              </div>
            </div>
          </div>

          {error && <div className="alert alert-error">{error}</div>}

          {loading ? (
            <div className="loading-container">
              <div className="spinner"></div>
              <p>Loading hospital metrics...</p>
            </div>
          ) : (
            <HospitalStatsTable stats={filteredStats} onRefresh={fetchStats} />
          )}
        </div>
      </main>
    </div>
  );
}

export default HospitalMetrics;
