import React, { useState, useEffect } from 'react';
import { doctorAPI, appointmentAPI } from '../services/api';

/**
 * PatientDashboard — the main page for logged-in patients.
 *
 * TWO TABS:
 *   1. "Book Appointment" — Browse doctors, select one, fill booking form
 *   2. "My Appointments" — View all appointments the patient has booked
 *
 * REACT CONCEPTS:
 *   - useEffect: runs side effects (API calls) after the component renders
 *   - Multiple useState hooks: each manages a different piece of state
 *   - Conditional rendering: show different content based on state
 *   - Event-driven state updates
 *
 * DATA FLOW:
 *   Component mounts → useEffect runs → fetch doctors + appointments from API
 *   → setState with results → React re-renders with the data
 */
function PatientDashboard() {
  // Read the logged-in user from localStorage
  const user = JSON.parse(localStorage.getItem('user'));

  // Which tab is active
  const [activeTab, setActiveTab] = useState('book');

  // List of all doctors (loaded from API)
  const [doctors, setDoctors] = useState([]);

  // The doctor the patient has clicked/selected
  const [selectedDoctor, setSelectedDoctor] = useState(null);

  // Patient's existing appointments
  const [appointments, setAppointments] = useState([]);

  // Booking form fields
  const [bookingForm, setBookingForm] = useState({
    appointmentDate: '',
    appointmentTime: '',
    reason: '',
  });

  // UI state
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  /**
   * useEffect with empty dependency array [] runs ONCE after first render.
   * This is equivalent to "componentDidMount" in class components.
   * We use it to load initial data from the API.
   */
  useEffect(() => {
    fetchDoctors();
    fetchMyAppointments();
  }, []);  // [] means: only run on mount, not on every re-render

  const fetchDoctors = async () => {
    try {
      const response = await doctorAPI.getAll();
      setDoctors(response.data);
    } catch (err) {
      setError('Failed to load doctors. Please refresh the page.');
    }
  };

  const fetchMyAppointments = async () => {
    try {
      const response = await appointmentAPI.getMyAppointments();
      setAppointments(response.data);
    } catch (err) {
      console.error('Failed to load appointments:', err);
    }
  };

  // Update booking form state as user types
  const handleBookingChange = (e) => {
    setBookingForm({ ...bookingForm, [e.target.name]: e.target.value });
  };

  const handleBookAppointment = async (e) => {
    e.preventDefault();

    if (!selectedDoctor) {
      setError('Please select a doctor first.');
      return;
    }

    setLoading(true);
    setError('');
    setSuccess('');

    try {
      await appointmentAPI.book({
        doctorId: selectedDoctor.id,
        appointmentDate: bookingForm.appointmentDate,
        appointmentTime: bookingForm.appointmentTime + ':00',  // API expects HH:MM:SS
        reason: bookingForm.reason,
      });

      setSuccess(`Appointment booked with ${selectedDoctor.name}!`);

      // Reset the form
      setBookingForm({ appointmentDate: '', appointmentTime: '', reason: '' });
      setSelectedDoctor(null);

      // Refresh the appointments list (so My Appointments tab shows the new one)
      fetchMyAppointments();

      // Switch to appointments tab after short delay
      setTimeout(() => {
        setActiveTab('appointments');
        setSuccess('');
      }, 1800);

    } catch (err) {
      const message = err.response?.data?.error || 'Failed to book appointment. Please try again.';
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  // Map status to CSS class for the badge
  const getStatusBadge = (status) => {
    const classes = {
      SCHEDULED: 'badge badge-scheduled',
      COMPLETED: 'badge badge-completed',
      CANCELLED: 'badge badge-cancelled',
    };
    return classes[status] || 'badge badge-scheduled';
  };

  // Today's date in YYYY-MM-DD format (to set min date on date picker)
  const today = new Date().toISOString().split('T')[0];

  return (
    <div>
      <h1>Patient Dashboard</h1>
      <p style={{ color: '#718096', marginBottom: '24px' }}>
        Welcome, <strong>{user.name}</strong>! Book appointments and track your visits.
      </p>

      {/* Tab navigation */}
      <div className="tabs">
        <button
          className={`tab ${activeTab === 'book' ? 'active' : ''}`}
          onClick={() => { setActiveTab('book'); setError(''); setSuccess(''); }}
        >
          📅 Book Appointment
        </button>
        <button
          className={`tab ${activeTab === 'appointments' ? 'active' : ''}`}
          onClick={() => { setActiveTab('appointments'); setError(''); setSuccess(''); }}
        >
          📋 My Appointments ({appointments.length})
        </button>
      </div>

      {/* =============================================
          TAB 1: Book Appointment
         ============================================= */}
      {activeTab === 'book' && (
        <div>
          {error   && <div className="alert alert-error">{error}</div>}
          {success && <div className="alert alert-success">{success}</div>}

          {/* Step 1: Choose a doctor */}
          <div className="card">
            <h3>Step 1 — Choose a Doctor</h3>
            <p style={{ color: '#718096', fontSize: '14px', marginBottom: '14px' }}>
              Click a doctor card to select them for your appointment.
            </p>

            <div className="doctor-grid">
              {doctors.map((doctor) => (
                <div
                  key={doctor.id}
                  className={`doctor-card ${selectedDoctor?.id === doctor.id ? 'selected' : ''}`}
                  onClick={() => {
                    setSelectedDoctor(doctor);
                    setError('');
                  }}
                >
                  <div className="doctor-name">👨‍⚕️ {doctor.name}</div>
                  <div className="doctor-specialty">{doctor.specialty}</div>
                  {selectedDoctor?.id === doctor.id && (
                    <div style={{ color: '#38a169', fontSize: '12px', marginTop: '8px', fontWeight: '700' }}>
                      ✓ Selected
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>

          {/* Step 2: Fill booking form — only shown after selecting a doctor */}
          {selectedDoctor && (
            <div className="card">
              <h3>Step 2 — Choose Date & Time</h3>
              <p style={{ color: '#4a5568', marginBottom: '18px', fontSize: '14px' }}>
                Booking with: <strong>{selectedDoctor.name}</strong> &nbsp;·&nbsp; {selectedDoctor.specialty}
              </p>

              <form onSubmit={handleBookAppointment}>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                  <div className="form-group">
                    <label>Date</label>
                    <input
                      type="date"
                      name="appointmentDate"
                      value={bookingForm.appointmentDate}
                      onChange={handleBookingChange}
                      min={today}   // Can't book in the past
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label>Time</label>
                    <input
                      type="time"
                      name="appointmentTime"
                      value={bookingForm.appointmentTime}
                      onChange={handleBookingChange}
                      required
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label>Reason for Visit</label>
                  <input
                    type="text"
                    name="reason"
                    placeholder="e.g., Regular checkup, Chest pain, Follow-up visit"
                    value={bookingForm.reason}
                    onChange={handleBookingChange}
                    required
                  />
                </div>

                <div style={{ display: 'flex', gap: '12px' }}>
                  <button
                    type="submit"
                    className="btn btn-success"
                    disabled={loading}
                  >
                    {loading ? 'Booking...' : '✓ Confirm Appointment'}
                  </button>
                  <button
                    type="button"
                    className="btn btn-danger"
                    onClick={() => setSelectedDoctor(null)}
                  >
                    ✗ Cancel
                  </button>
                </div>
              </form>
            </div>
          )}
        </div>
      )}

      {/* =============================================
          TAB 2: My Appointments
         ============================================= */}
      {activeTab === 'appointments' && (
        <div className="card">
          <h3>Your Appointments</h3>

          {appointments.length === 0 ? (
            <div className="empty-state">
              <p>You haven't booked any appointments yet.</p>
              <button
                className="btn btn-primary"
                onClick={() => setActiveTab('book')}
              >
                Book Your First Appointment
              </button>
            </div>
          ) : (
            <div className="table-container">
              <table>
                <thead>
                  <tr>
                    <th>Doctor</th>
                    <th>Specialty</th>
                    <th>Date</th>
                    <th>Time</th>
                    <th>Reason</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {appointments.map((apt) => (
                    <tr key={apt.id}>
                      <td><strong>{apt.doctorName}</strong></td>
                      <td>{apt.doctorSpecialty}</td>
                      <td>{apt.appointmentDate}</td>
                      <td>{apt.appointmentTime}</td>
                      <td>{apt.reason}</td>
                      <td>
                        <span className={getStatusBadge(apt.status)}>
                          {apt.status}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default PatientDashboard;
