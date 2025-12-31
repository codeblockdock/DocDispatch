import React, { createContext, useContext, useState, useEffect } from 'react';
import { authAPI } from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    checkAuth();
  }, []);

  const checkAuth = async () => {
    const token = localStorage.getItem('hospitalToken');
    const hospitalData = localStorage.getItem('hospitalData');
    
    if (token && hospitalData) {
      try {
        await authAPI.verify();
        setUser(JSON.parse(hospitalData));
      } catch (err) {
        localStorage.removeItem('hospitalToken');
        localStorage.removeItem('hospitalData');
        setUser(null);
      }
    }
    setLoading(false);
  };

  const login = async (hospitalId, password) => {
    try {
      setError(null);
      const response = await authAPI.login(hospitalId, password);
      
      if (response.data.success) {
        const { token, ...userData } = response.data;
        localStorage.setItem('hospitalToken', token);
        localStorage.setItem('hospitalData', JSON.stringify(userData));
        setUser(userData);
        return { success: true };
      } else {
        setError(response.data.message || 'Login failed');
        return { success: false, message: response.data.message };
      }
    } catch (err) {
      const message = err.response?.data?.message || 'Login failed. Please try again.';
      setError(message);
      return { success: false, message };
    }
  };

  const logout = () => {
    localStorage.removeItem('hospitalToken');
    localStorage.removeItem('hospitalData');
    setUser(null);
  };

  const value = {
    user,
    isAuthenticated: !!user,
    loading,
    error,
    login,
    logout,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
