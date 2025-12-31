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
    if (error.response?.status === 401) {
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
    api.post('/api/hospital/login', { hospitalId, password }),
  
  register: (data) => 
    api.post('/api/hospital/register', data),
  
  verify: () => 
    api.get('/api/hospital/verify'),
};

// Patient APIs
export const patientAPI = {
  getAll: (params = {}) => {
    const queryParams = new URLSearchParams();
    if (params.search) queryParams.append('search', params.search);
    if (params.disease) queryParams.append('disease', params.disease);
    if (params.city) queryParams.append('city', params.city);
    if (params.pincode) queryParams.append('pincode', params.pincode);
    
    const queryString = queryParams.toString();
    return api.get(`/api/hospital/patients${queryString ? `?${queryString}` : ''}`);
  },
  
  getById: (id) => 
    api.get(`/api/hospital/patients/${id}`),
  
  delete: (id) => 
    api.delete(`/api/hospital/patients/${id}`),
  
  // Attend/update patient
  attend: (queryId, data) =>
    api.post('/attend', { queryId, ...data }),
};

// Stats API
export const statsAPI = {
  get: () => api.get('/api/hospital/stats'),
};

export default api;
