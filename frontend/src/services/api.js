import axios from 'axios';

/**
 * api.js — Centralized API service layer for the frontend.
 *
 * WHY A SEPARATE API FILE?
 *   Instead of writing fetch/axios calls scattered across every component,
 *   we define all API calls in ONE place.
 *
 *   Benefits:
 *   - Change the base URL in one place, not everywhere
 *   - Add global headers (like X-User-Id) automatically via interceptors
 *   - Easy to see all available API endpoints
 *
 * HOW THE URL WORKS:
 *   In Docker:     nginx proxies /api/* → backend:8080 (relative URL = "")
 *   In local dev:  REACT_APP_API_URL=http://localhost:8080 (from .env.development)
 */
const API_BASE_URL = process.env.REACT_APP_API_URL || '';

// Create a reusable axios instance with default settings
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Request Interceptor
 *
 * This function runs BEFORE every API call is sent.
 * It reads the logged-in user from localStorage and
 * attaches their ID as the X-User-Id header.
 *
 * The backend uses this header to identify who is making the request.
 * (In production, this would be a JWT Bearer token instead.)
 */
api.interceptors.request.use((config) => {
  const user = JSON.parse(localStorage.getItem('user') || 'null');
  if (user && user.id) {
    config.headers['X-User-Id'] = user.id;
  }
  return config;
});

// =============================================================
// AUTH API — Registration and Login
// =============================================================
export const authAPI = {
  // POST /api/auth/register  — Create a new patient account
  register: (data) => api.post('/api/auth/register', data),

  // POST /api/auth/login     — Login with email + password
  login: (data) => api.post('/api/auth/login', data),
};

// =============================================================
// DOCTOR API — Listing available doctors
// =============================================================
export const doctorAPI = {
  // GET /api/doctors  — Returns all doctors
  getAll: () => api.get('/api/doctors'),
};

// =============================================================
// APPOINTMENT API — Booking and managing appointments
// =============================================================
export const appointmentAPI = {
  // POST /api/appointments         — Patient books a new appointment
  book: (data) => api.post('/api/appointments', data),

  // GET /api/appointments/my       — Patient views their own appointments
  getMyAppointments: () => api.get('/api/appointments/my'),

  // GET /api/appointments/doctor   — Doctor views their assigned appointments
  getDoctorAppointments: () => api.get('/api/appointments/doctor'),

  // PUT /api/appointments/{id}/status — Doctor updates appointment status
  updateStatus: (id, status) => api.put(`/api/appointments/${id}/status`, { status }),
};

export default api;
