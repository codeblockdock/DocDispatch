import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { patientAPI, statsAPI } from '../services/api';
import Header from '../components/Header';
import StatsCards from '../components/StatsCards';
import PatientTable from '../components/PatientTable';
import PatientModal from '../components/PatientModal';
import AttendModal from '../components/AttendModal';
import ReceiptModal from '../components/ReceiptModal';
import DispatchSection from '../components/DispatchSection';
import './Dashboard.css';

function Dashboard() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [patients, setPatients] = useState([]);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  // Filters
  const [filters, setFilters] = useState({
    search: '',
    city: '',
    pincode: '',
    state: '',
    riskFactor: '',
  });
  
  // Modals
  const [selectedPatient, setSelectedPatient] = useState(null);
  const [showViewModal, setShowViewModal] = useState(false);
  const [showAttendModal, setShowAttendModal] = useState(false);
  const [showReceiptModal, setShowReceiptModal] = useState(false);
  
  // Flash message for dispatch
  const [flashMessage, setFlashMessage] = useState(null);

  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      setError('');
      
      const [patientsRes, statsRes] = await Promise.all([
        patientAPI.getAll(filters),
        statsAPI.get(),
      ]);
      
      setPatients(patientsRes.data);
      setStats(statsRes.data);
    } catch (err) {
      console.error('Error fetching data:', err);
      setError('Failed to load data. Please try again.');
    } finally {
      setLoading(false);
    }
  }, [filters]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({ ...prev, [name]: value }));
  };

  const handleSearch = (e) => {
    e.preventDefault();
    fetchData();
  };

  const handleClearFilters = () => {
    setFilters({
      search: '',
      city: '',
      pincode: '',
      riskFactor: '',
    });
  };

  const handleView = async (patient) => {
    try {
      const response = await patientAPI.getById(patient.id);
      setSelectedPatient(response.data);
      setShowViewModal(true);
    } catch (err) {
      console.error('Error fetching patient details:', err);
    }
  };

  const handleAttend = (patient) => {
    setSelectedPatient(patient);
    setShowAttendModal(true);
  };

  const handleViewReceipt = (patient) => {
    setSelectedPatient(patient);
    setShowReceiptModal(true);
  };

  const handleDispatch = async (group) => {
    try {
      // Get hospital data from localStorage
      const storedData = localStorage.getItem('hospitalData');
      let hospitalData = {
        doctorName: 'Dispatched Doctor',
        hospitalName: 'Dispatch Center',
        city: group.city,
        state: group.state,
      };
      
      if (storedData) {
        try {
          const data = JSON.parse(storedData);
          hospitalData = {
            doctorName: data.doctorName || 'Dispatched Doctor',
            hospitalName: data.name || data.hospitalName || 'Dispatch Center',
            city: data.city || group.city,
            state: data.state || group.state,
          };
        } catch (e) {
          console.error('Error parsing hospital data:', e);
        }
      }

      // Prepare mass attend data
      const massAttendData = {
        queryIds: group.patients.map(p => p.id),
        doctor: hospitalData.doctorName,
        hospital: hospitalData.hospitalName,
        city: hospitalData.city,
        state: hospitalData.state,
        diagnosis: 'Field Assessment Required',
        treatment: 'Doctor Dispatched - In-person examination scheduled',
        appointment: 'Doctor En Route',
        advice: 'Doctor has been dispatched to your location. Please be available for examination.',
        doctorsDispatched: group.doctorsNeeded,
        location: group.village,
        pincode: group.pincode,
      };

      await patientAPI.massAttend(massAttendData);
      
      // Show flash message
      setFlashMessage({
        location: group.village,
        pincode: group.pincode,
        doctors: group.doctorsNeeded,
        patients: group.patientCount,
      });
      
      // Auto-hide flash message after 3 seconds
      setTimeout(() => {
        setFlashMessage(null);
      }, 3000);
      
      // Refresh data
      fetchData();
    } catch (err) {
      console.error('Error dispatching doctor:', err);
      setError('Failed to dispatch doctor. Please try again.');
    }
  };

  return (
    <div className="dashboard">
      <Header />
      
      {/* Flash Message for Doctor Dispatch */}
      {flashMessage && (
        <div className="dispatch-flash-message">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
            <polyline points="22 4 12 14.01 9 11.01"/>
          </svg>
          <div>
            <span className="flash-text">
              🚑 Doctor Dispatched in Your Location!
            </span>
            <span className="flash-location">
              {' '}• {flashMessage.doctors} doctor(s) sent to {flashMessage.location} ({flashMessage.pincode}) for {flashMessage.patients} patient(s)
            </span>
          </div>
        </div>
      )}
      
      <main className="dashboard-main">
        <div className="container">
          {/* Page Header */}
          <div className="dashboard-header">
            <div>
              <h1 className="dashboard-title">Patient Dashboard</h1>
              <p className="dashboard-subtitle">
                Viewing patients from <span className="state-badge">{user?.state}</span>
              </p>
            </div>
            <div className="dashboard-actions">
              {user?.isAdmin && (
                <button 
                  className="btn btn-secondary" 
                  onClick={() => navigate('/hospital-metrics')}
                  style={{ marginRight: '1rem' }}
                >
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" style={{ marginRight: '0.5rem' }}>
                    <path d="M12 20v-6M6 20V10M18 20V4"/>
                  </svg>
                  Hospital Performance
                </button>
              )}
              <button className="btn btn-primary" onClick={fetchData}>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M23 4v6h-6"/>
                  <path d="M1 20v-6h6"/>
                  <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/>
                </svg>
                Refresh
              </button>
            </div>
          </div>

          {/* Stats Cards */}
          <StatsCards stats={stats} loading={loading} isAdmin={user?.isAdmin} />

          {/* Dispatch Section - Near Hospital Performance */}
          {user?.isAdmin && (
            <DispatchSection 
              patients={patients} 
              onDispatch={handleDispatch}
              onRefresh={fetchData}
            />
          )}

          {/* Filters */}
          <div className="filters-card">
            <form onSubmit={handleSearch} className="filters-form">
              <div className="filter-group">
                <input
                  type="text"
                  name="search"
                  value={filters.search}
                  onChange={handleFilterChange}
                  className="form-input"
                  placeholder="Search by patient name..."
                />
              </div>
              <div className="filter-group">
                <input
                  type="text"
                  name="city"
                  value={filters.city}
                  onChange={handleFilterChange}
                  className="form-input"
                  placeholder="Filter by city..."
                />
              </div>
              <div className="filter-group">
                <input
                  type="text"
                  name="pincode"
                  value={filters.pincode}
                  onChange={handleFilterChange}
                  className="form-input"
                  placeholder="Filter by pincode..."
                />
              </div>
              <div className="filter-group">
                <select
                  name="riskFactor"
                  value={filters.riskFactor}
                  onChange={handleFilterChange}
                  className="form-input"
                >
                  <option value="">All Risk Factors</option>
                  <option value="low">Low Risk</option>
                  <option value="medium">Medium Risk</option>
                  <option value="high">High Risk</option>
                  <option value="priority">Priority</option>
                </select>
              </div>
              <button type="submit" className="btn btn-primary">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <circle cx="11" cy="11" r="8"/>
                  <line x1="21" y1="21" x2="16.65" y2="16.65"/>
                </svg>
                Search
              </button>
              <button type="button" className="btn btn-secondary" onClick={handleClearFilters}>
                Clear
              </button>
            </form>
          </div>

          {/* Error Display */}
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

          {/* Patient Table */}
          <PatientTable 
            patients={patients}
            loading={loading}
            onView={handleView}
            onAttend={handleAttend}
            onViewReceipt={handleViewReceipt}
          />
        </div>
      </main>

      {/* View Patient Modal */}
      {showViewModal && selectedPatient && (
        <PatientModal 
          patient={selectedPatient}
          onClose={() => {
            setShowViewModal(false);
            setSelectedPatient(null);
          }}
          onRefresh={fetchData}
        />
      )}

      {/* Attend Patient Modal */}
      {showAttendModal && selectedPatient && (
        <AttendModal 
          patient={selectedPatient}
          onClose={() => {
            setShowAttendModal(false);
            setSelectedPatient(null);
          }}
          onRefresh={fetchData}
        />
      )}

      {/* View Receipt Modal */}
      {showReceiptModal && selectedPatient && (
        <ReceiptModal 
          patient={selectedPatient}
          onClose={() => {
            setShowReceiptModal(false);
            setSelectedPatient(null);
          }}
        />
      )}
    </div>
  );
}

export default Dashboard;
