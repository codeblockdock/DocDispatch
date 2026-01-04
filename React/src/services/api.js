import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8081';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add auth token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('hospitalToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle auth errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && !window.location.pathname.includes('/login')) {
      localStorage.removeItem('hospitalToken');
      localStorage.removeItem('hospitalData');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth APIs
export const authAPI = {
  login: (hospitalId, password) => 
    api.post('/hospital/login', { hospitalId, password }),
  
  register: (data) => 
    api.post('/hospital/register', data),
  
  verify: () => 
    api.get('/hospital/verify'),
};

// Patient APIs
export const patientAPI = {
  getAll: (params = {}) => {
    const queryParams = new URLSearchParams();
    if (params.search) queryParams.append('search', params.search);
    if (params.city) queryParams.append('city', params.city);
    if (params.pincode) queryParams.append('pincode', params.pincode);
    if (params.riskFactor) queryParams.append('riskFactor', params.riskFactor);
    
    const queryString = queryParams.toString();
    return api.get(`/hospital/patients${queryString ? `?${queryString}` : ''}`);
  },
  
  getById: (id) => 
    api.get(`/hospital/patients/${id}`),
  
  delete: (id) => 
    api.delete(`/hospital/patients/${id}`),
  
  // Attend/update patient
  attend: (queryId, data) =>
    api.post('/attend', { queryId, ...data }),
};

// Stats API
export const statsAPI = {
  get: () => api.get('/hospital/stats'),
  getAllHospitalStats: () => api.get('/hospital/all-hospital-stats'),
  toggleHospitalStatus: (hospitalId) => api.post(`/hospital/toggle-status/${hospitalId}`),
};

export default api;
