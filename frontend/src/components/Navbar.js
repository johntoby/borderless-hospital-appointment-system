import React from 'react';
import { useNavigate } from 'react-router-dom';

/**
 * Navbar Component
 *
 * Displayed on every page (rendered in App.js above <Routes>).
 *
 * Behavior:
 *   - If logged in: shows the user's name, role, and a Logout button
 *   - If not logged in: shows Login and Register buttons
 *
 * Logout:
 *   Removes the user from localStorage and redirects to /login.
 *   This effectively "ends the session" in our simple auth system.
 *
 * useNavigate():
 *   A React Router hook that returns a function to programmatically
 *   navigate to a different URL (like history.push in older versions).
 */
function Navbar() {
  const navigate = useNavigate();

  // Read current user from localStorage
  const user = JSON.parse(localStorage.getItem('user') || 'null');

  const handleLogout = () => {
    localStorage.removeItem('user');   // Clear the "session"
    navigate('/login');                // Redirect to login page
  };

  const handleLogoClick = () => {
    if (!user) return;
    // Navigate to the correct dashboard based on role
    navigate(user.role === 'DOCTOR' ? '/doctor-dashboard' : '/patient-dashboard');
  };

  return (
    <nav style={styles.nav}>
      {/* Brand / Logo */}
      <div style={styles.brand} onClick={handleLogoClick}>
        🏥 Borderless Hospital
      </div>

      {/* Right side: user info or auth buttons */}
      {user ? (
        <div style={styles.userSection}>
          <span style={styles.userInfo}>
            <span style={styles.userName}>{user.name}</span>
            <span style={styles.userRole}>{user.role}</span>
          </span>
          <button onClick={handleLogout} style={styles.logoutBtn}>
            Logout
          </button>
        </div>
      ) : (
        <div style={styles.authLinks}>
          <button onClick={() => navigate('/login')} style={styles.linkBtn}>
            Login
          </button>
          <button onClick={() => navigate('/register')} style={styles.registerBtn}>
            Register
          </button>
        </div>
      )}
    </nav>
  );
}

const styles = {
  nav: {
    backgroundColor: '#1a365d',
    color: 'white',
    padding: '14px 28px',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    boxShadow: '0 2px 6px rgba(0,0,0,0.25)',
    position: 'sticky',
    top: 0,
    zIndex: 100,
  },
  brand: {
    fontSize: '20px',
    fontWeight: '800',
    letterSpacing: '0.3px',
    cursor: 'pointer',
  },
  userSection: {
    display: 'flex',
    alignItems: 'center',
    gap: '16px',
  },
  userInfo: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'flex-end',
    gap: '2px',
  },
  userName: {
    fontSize: '14px',
    fontWeight: '600',
  },
  userRole: {
    fontSize: '11px',
    backgroundColor: 'rgba(255,255,255,0.2)',
    padding: '1px 8px',
    borderRadius: '10px',
    textTransform: 'uppercase',
    letterSpacing: '0.5px',
  },
  logoutBtn: {
    backgroundColor: 'rgba(255,255,255,0.15)',
    color: 'white',
    border: '1px solid rgba(255,255,255,0.35)',
    padding: '7px 16px',
    borderRadius: '7px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '600',
    transition: 'background 0.2s',
  },
  authLinks: {
    display: 'flex',
    gap: '10px',
    alignItems: 'center',
  },
  linkBtn: {
    backgroundColor: 'transparent',
    color: 'white',
    border: '1px solid rgba(255,255,255,0.45)',
    padding: '7px 16px',
    borderRadius: '7px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '600',
  },
  registerBtn: {
    backgroundColor: '#4299e1',
    color: 'white',
    border: 'none',
    padding: '7px 16px',
    borderRadius: '7px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '600',
  },
};

export default Navbar;
