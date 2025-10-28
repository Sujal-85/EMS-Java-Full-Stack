import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ThemeProvider } from './context/ThemeContext';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';

import ProtectedRoute from './components/ProtectedRoute';
import Navbar from './components/Navbar';
import Login from './pages/Login';
import ForgotPassword from './pages/ForgotPassword';
import Dashboard from './pages/Dashboard';
import Employees from './pages/Employees';
import Attendance from './pages/Attendance';
import Leaves from './pages/Leaves';
import Salaries from './pages/Salaries';
import Departments from './pages/Departments';
import AuditLogs from './pages/AuditLogs';

const App = () => {
  return (
    <Router>
      <ThemeProvider>
        <AuthProvider>
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
            
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            
            <Route
              path="/dashboard"
              element={
                <ProtectedRoute>
                  <Navbar />
                  <Dashboard />
                </ProtectedRoute>
              }
            />
            
            <Route
              path="/employees"
              element={
                <ProtectedRoute roles={['ADMIN', 'HR']}>
                  <Navbar />
                  <Employees />
                </ProtectedRoute>
              }
            />
            
            <Route
              path="/attendance"
              element={
                <ProtectedRoute>
                  <Navbar />
                  <Attendance />
                </ProtectedRoute>
              }
            />
            
            <Route
              path="/leaves"
              element={
                <ProtectedRoute>
                  <Navbar />
                  <Leaves />
                </ProtectedRoute>
              }
            />
            
            <Route
              path="/salaries"
              element={
                <ProtectedRoute roles={['ADMIN', 'HR']}>
                  <Navbar />
                  <Salaries />
                </ProtectedRoute>
              }
            />
            
            <Route
              path="/departments"
              element={
                <ProtectedRoute roles={['ADMIN', 'HR']}>
                  <Navbar />
                  <Departments />
                </ProtectedRoute>
              }
            />
            
            <Route
              path="/audit-logs"
              element={
                <ProtectedRoute roles={['ADMIN']}>
                  <Navbar />
                  <AuditLogs />
                </ProtectedRoute>
              }
            />
            
            <Route path="/unauthorized" element={<div className="container mt-5"><h2>Unauthorized Access</h2><p>You don't have permission to access this page.</p></div>} />
          </Routes>
          
          <ToastContainer
            position="top-right"
            autoClose={3000}
            hideProgressBar={false}
            newestOnTop
            closeOnClick
            rtl={false}
            pauseOnFocusLoss
            draggable
            pauseOnHover
          />
        </AuthProvider>
      </ThemeProvider>
    </Router>
  );
};

export default App;
