import React, { useState, useEffect } from 'react';
import { appointmentAPI } from '../services/api';

/**
 * DoctorDashboard — the main page for logged-in doctors.
 *
 * FEATURES:
 *   - Summary cards showing appointment counts by status
 *   - Table of all assigned appointments
 *   - Inline action buttons to mark as Completed or Cancelled
 *
 * OPTIMISTIC UI PATTERN:
 *   After updating a status, we update the LOCAL state immediately
 *   (using .map() to replace the updated appointment) instead of
 *   re-fetching the entire list from the API.
 *   This makes the UI feel instant and responsive.
 */
function DoctorDashboard() {
  const user = JSON.parse(localStorage.getItem('user'));

  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [updatingId, setUpdatingId] = useState(null); // Which row is being updated

  // Load doctor's appointments when the page first loads
  useEffect(() => {
    fetchAppointments();
  }, []);

  const fetchAppointments = async () => {
    setLoading(true);
    try {
      const response = await appointmentAPI.getDoctorAppointments();
      setAppointments(response.data);
    } catch (err) {
      setError('Failed to load appointments. Please refresh.');
    } finally {
      setLoading(false);
    }
  };

  /**
   * Update appointment status.
   *
   * Steps:
   *   1. Mark this row as "updating" (disables its buttons)
   *   2. Call PUT /api/appointments/{id}/status
   *   3. Update the local state without re-fetching everything
   *   4. Show a success message, then clear it after 3 seconds
   */
  const handleStatusUpdate = async (appointmentId, newStatus) => {
    setUpdatingId(appointmentId);
    setError('');
    setSuccess('');

    try {
      const response = await appointmentAPI.updateStatus(appointmentId, newStatus);

      // Update just this one appointment in local state (no full refetch needed)
      setAppointments(prev =>
        prev.map(apt =>
          apt.id === appointmentId
            ? { ...apt, status: response.data.status }   // spread operator updates only the status
            : apt
        )
      );

      setSuccess(`Appointment #${appointmentId} marked as ${newStatus}.`);
      setTimeout(() => setSuccess(''), 3000);

    } catch (err) {
      setError(err.response?.data?.error || 'Failed to update status.');
    } finally {
      setUpdatingId(null);
    }
  };

  // CSS class for status badge
  const getStatusBadge = (status) => {
    const map = {
      SCHEDULED: 'badge badge-scheduled',
      COMPLETED:  'badge badge-completed',
      CANCELLED:  'badge badge-cancelled',
    };
    return map[status] || 'badge badge-scheduled';
  };

  // Compute summary counts from the appointments array
  const counts = {
    scheduled: appointments.filter(a => a.status === 'SCHEDULED').length,
    completed:  appointments.filter(a => a.status === 'COMPLETED').length,
    cancelled:  appointments.filter(a => a.status === 'CANCELLED').length,
    total:      appointments.length,
  };

  return (
    <div>
      <h1>Doctor Dashboard</h1>
      <p style={{ color: '#718096', marginBottom: '24px' }}>
        Welcome, <strong>{user.name}</strong>! Here are your patient appointments.
      </p>

      {/* Summary stat cards */}
      <div className="summary-grid">
        <div className="summary-card" style={{ borderTop: '4px solid #4299e1' }}>
          <div className="summary-number">{counts.scheduled}</div>
          <div className="summary-label">Scheduled</div>
        </div>
        <div className="summary-card" style={{ borderTop: '4px solid #48bb78' }}>
          <div className="summary-number">{counts.completed}</div>
          <div className="summary-label">Completed</div>
        </div>
        <div className="summary-card" style={{ borderTop: '4px solid #fc8181' }}>
          <div className="summary-number">{counts.cancelled}</div>
          <div className="summary-label">Cancelled</div>
        </div>
        <div className="summary-card" style={{ borderTop: '4px solid #ed8936' }}>
          <div className="summary-number">{counts.total}</div>
          <div className="summary-label">Total</div>
        </div>
      </div>

      {/* Alerts */}
      {error   && <div className="alert alert-error">{error}</div>}
      {success && <div className="alert alert-success">{success}</div>}

      {/* Appointments table */}
      <div className="card">
        <h3>All Appointments</h3>

        {loading ? (
          <div className="loading">Loading appointments...</div>
        ) : appointments.length === 0 ? (
          <div className="empty-state">
            <p>No appointments assigned to you yet.</p>
            <p style={{ fontSize: '13px', marginTop: '8px' }}>
              Patients will appear here after they book with you.
            </p>
          </div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>#</th>
                  <th>Patient</th>
                  <th>Date</th>
                  <th>Time</th>
                  <th>Reason</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {appointments.map((apt) => (
                  <tr key={apt.id}>
                    <td style={{ color: '#a0aec0' }}>{apt.id}</td>
                    <td><strong>{apt.patientName}</strong></td>
                    <td>{apt.appointmentDate}</td>
                    <td>{apt.appointmentTime}</td>
                    <td>{apt.reason}</td>
                    <td>
                      <span className={getStatusBadge(apt.status)}>
                        {apt.status}
                      </span>
                    </td>
                    <td>
                      {/* Only show action buttons for SCHEDULED appointments */}
                      {apt.status === 'SCHEDULED' ? (
                        <div style={{ display: 'flex', gap: '8px' }}>
                          <button
                            className="btn btn-success"
                            style={{ padding: '4px 12px', fontSize: '12px' }}
                            disabled={updatingId === apt.id}
                            onClick={() => handleStatusUpdate(apt.id, 'COMPLETED')}
                          >
                            {updatingId === apt.id ? '...' : 'Complete'}
                          </button>
                          <button
                            className="btn btn-danger"
                            style={{ padding: '4px 12px', fontSize: '12px' }}
                            disabled={updatingId === apt.id}
                            onClick={() => handleStatusUpdate(apt.id, 'CANCELLED')}
                          >
                            {updatingId === apt.id ? '...' : 'Cancel'}
                          </button>
                        </div>
                      ) : (
                        // No actions for completed or cancelled appointments
                        <span style={{ color: '#cbd5e0', fontSize: '13px' }}>—</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

export default DoctorDashboard;
