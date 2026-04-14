import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authAPI } from '../services/api';

/**
 * Login Page
 *
 * REACT CONCEPTS DEMONSTRATED:
 *   - useState: manages form fields and UI state (error, loading)
 *   - Controlled inputs: input value tied to state via value + onChange
 *   - Event handling: handleChange updates state on every keystroke
 *   - Async/await: handleSubmit calls the API and waits for the response
 *   - useNavigate: programmatic navigation after successful login
 *
 * FLOW:
 *   1. User fills in email + password
 *   2. Submit calls authAPI.login()
 *   3. On success: save user to localStorage, navigate to dashboard
 *   4. On failure: show the error message from the API
 */
function Login() {
  const navigate = useNavigate();

  // Form data state — object with all form fields
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });

  // UI state
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  // Called on every keystroke in any input field
  // [e.target.name] is a computed property — it uses the input's name attribute
  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setError('');  // Clear error when user starts typing
  };

  const handleSubmit = async (e) => {
    e.preventDefault();  // Prevent browser from submitting the form (page reload)
    setLoading(true);
    setError('');

    try {
      // Call POST /api/auth/login
      const response = await authAPI.login(formData);
      const user = response.data;

      // Save user info to localStorage (our simple "session")
      localStorage.setItem('user', JSON.stringify(user));

      // Navigate to the correct dashboard based on role
      if (user.role === 'DOCTOR') {
        navigate('/doctor-dashboard');
      } else {
        navigate('/patient-dashboard');
      }
    } catch (err) {
      // Extract the error message from the API response
      // err.response.data is the JSON body returned by our GlobalExceptionHandler
      const message = err.response?.data?.error || 'Login failed. Please check your credentials.';
      setError(message);
    } finally {
      setLoading(false);  // Always hide loading spinner, success or failure
    }
  };

  return (
    <div style={styles.container}>
      <div className="card" style={styles.card}>
        {/* Header */}
        <div style={styles.header}>
          <div style={styles.icon}>🏥</div>
          <h2 style={styles.title}>Welcome Back</h2>
          <p style={styles.subtitle}>Sign in to your account</p>
        </div>

        {/* Error message */}
        {error && <div className="alert alert-error">{error}</div>}

        {/* Login Form */}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="email">Email Address</label>
            <input
              id="email"
              type="email"
              name="email"
              placeholder="you@example.com"
              value={formData.email}
              onChange={handleChange}
              required
              autoFocus
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              name="password"
              placeholder="Enter your password"
              value={formData.password}
              onChange={handleChange}
              required
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary btn-block"
            disabled={loading}
          >
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>

        {/* Link to registration */}
        <p style={styles.registerLink}>
          Don't have an account?{' '}
          <Link to="/register" style={styles.link}>Register as a patient</Link>
        </p>

        {/* Sample credentials hint for teaching */}
        <div style={styles.hint}>
          <strong>🩺 Sample Doctor Credentials:</strong><br />
          <span style={{ fontFamily: 'monospace' }}>sarah.johnson@borderlesshospital.com</span><br />
          Password: <span style={{ fontFamily: 'monospace' }}>doctor123</span>
        </div>
      </div>
    </div>
  );
}

const styles = {
  container: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    minHeight: '80vh',
  },
  card: {
    width: '100%',
    maxWidth: '430px',
  },
  header: {
    textAlign: 'center',
    marginBottom: '24px',
  },
  icon: {
    fontSize: '40px',
    marginBottom: '8px',
  },
  title: {
    color: '#1a365d',
    marginBottom: '4px',
  },
  subtitle: {
    color: '#718096',
    fontSize: '14px',
  },
  registerLink: {
    textAlign: 'center',
    marginTop: '18px',
    color: '#718096',
    fontSize: '14px',
  },
  link: {
    color: '#4299e1',
    textDecoration: 'none',
    fontWeight: '600',
  },
  hint: {
    marginTop: '20px',
    padding: '14px',
    backgroundColor: '#ebf8ff',
    borderRadius: '8px',
    fontSize: '13px',
    color: '#2c5282',
    lineHeight: '1.7',
    borderLeft: '3px solid #4299e1',
  },
};

export default Login;
