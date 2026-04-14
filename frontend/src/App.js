import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import Login from './pages/Login';
import Register from './pages/Register';
import PatientDashboard from './pages/PatientDashboard';
import DoctorDashboard from './pages/DoctorDashboard';

/**
 * App.js — The root component and routing configuration.
 *
 * ROUTING WITH REACT ROUTER v6:
 *   <BrowserRouter> — enables HTML5 history-based routing
 *   <Routes>        — container for all route definitions
 *   <Route>         — maps a URL path to a component
 *   <Navigate>      — redirects to another path
 *
 * PROTECTED ROUTES:
 *   Some pages should only be accessible when logged in.
 *   The <ProtectedRoute> component checks localStorage for a
 *   logged-in user and redirects to /login if none is found.
 *   It also checks the user's role to prevent cross-role access.
 */

// Helper: Is there a logged-in user in localStorage?
const isLoggedIn = () => localStorage.getItem('user') !== null;

// Helper: What role does the logged-in user have?
const getUserRole = () => {
  const user = JSON.parse(localStorage.getItem('user') || 'null');
  return user ? user.role : null;
};

/**
 * ProtectedRoute Component
 *
 * Wraps routes that require authentication.
 * - If not logged in → redirect to /login
 * - If wrong role → redirect to the correct dashboard
 * - If OK → render the children (the actual page)
 */
const ProtectedRoute = ({ children, requiredRole }) => {
  if (!isLoggedIn()) {
    return <Navigate to="/login" replace />;
  }

  if (requiredRole && getUserRole() !== requiredRole) {
    // User is logged in but wrong role (e.g., doctor trying to access patient dashboard)
    const correctPath = getUserRole() === 'DOCTOR' ? '/doctor-dashboard' : '/patient-dashboard';
    return <Navigate to={correctPath} replace />;
  }

  return children;
};

function App() {
  return (
    <Router>
      {/* Navbar is rendered on every page */}
      <Navbar />

      <div className="main-content">
        <Routes>
          {/* Default: redirect root path to login */}
          <Route path="/" element={<Navigate to="/login" replace />} />

          {/* Public routes — no login required */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* Protected: only for PATIENT role */}
          <Route
            path="/patient-dashboard"
            element={
              <ProtectedRoute requiredRole="PATIENT">
                <PatientDashboard />
              </ProtectedRoute>
            }
          />

          {/* Protected: only for DOCTOR role */}
          <Route
            path="/doctor-dashboard"
            element={
              <ProtectedRoute requiredRole="DOCTOR">
                <DoctorDashboard />
              </ProtectedRoute>
            }
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
