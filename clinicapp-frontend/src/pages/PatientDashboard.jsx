import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../api/axiosInstance';

export default function PatientDashboard() {
  const { user, logout } = useAuth();
  const [doctors, setDoctors] = useState([]);
  const [selectedDoctorId, setSelectedDoctorId] = useState('');
  const [slots, setSlots] = useState([]);
  const [appointments, setAppointments] = useState([]);
  const [message, setMessage] = useState('');

  const loadDoctors = async () => {
    const res = await api.get('/doctors');
    setDoctors(res.data);
  };

  const loadAppointments = async () => {
    const res = await api.get('/appointments');
    setAppointments(res.data);
  };

  useEffect(() => {
    loadDoctors();
    loadAppointments();
  }, []);

  const loadSlots = async (doctorId) => {
    setSelectedDoctorId(doctorId);
    if (!doctorId) {
      setSlots([]);
      return;
    }
    const res = await api.get(`/doctors/${doctorId}/slots`);
    setSlots(res.data);
  };

  const handleBook = async (slotId) => {
    setMessage('');
    try {
      await api.post('/appointments', { slotId });
      setMessage('Booked successfully');
      loadSlots(selectedDoctorId);
      loadAppointments();
    } catch (err) {
      setMessage(err.response?.data?.error || 'Booking failed');
    }
  };

  const handleCancel = async (appointmentId) => {
    setMessage('');
    try {
      await api.delete(`/appointments/${appointmentId}`);
      setMessage('Cancelled successfully');
      loadAppointments();
      if (selectedDoctorId) loadSlots(selectedDoctorId);
    } catch (err) {
      setMessage(err.response?.data?.error || 'Cancel failed');
    }
  };

  const handleDownloadPdf = async (appointmentId) => {
    setMessage('');
    try {
      const res = await api.get(`/appointments/${appointmentId}/prescription/pdf`, {
        responseType: 'blob',
      });
      const url = window.URL.createObjectURL(new Blob([res.data], { type: 'application/pdf' }));
      window.open(url);
    } catch (err) {
      setMessage('No prescription available yet');
    }
  };

  return (
    <div>
      <h2>Patient Dashboard</h2>
      <p>Logged in as: {user.email}</p>
      <button onClick={logout}>Logout</button>
      {message && <p>{message}</p>}

      <hr />

      <h3>Browse Doctors</h3>
      <select value={selectedDoctorId} onChange={(e) => loadSlots(e.target.value)}>
        <option value="">-- Select a doctor --</option>
        {doctors.map((doc) => (
          <option key={doc.id} value={doc.id}>{doc.name}</option>
        ))}
      </select>

      <h3>Available Slots</h3>
      <ul>
        {slots.map((slot) => (
          <li key={slot.id}>
            {slot.startTime} – {slot.endTime}
            <button onClick={() => handleBook(slot.id)}>Book</button>
          </li>
        ))}
      </ul>

      <hr />

      <h3>My Appointments</h3>
      <ul>
        {appointments.map((appt) => (
          <li key={appt.id}>
            {appt.startTime} — Dr. {appt.doctorName} ({appt.status})
            {appt.status === 'CONFIRMED' && (
              <>
                <button onClick={() => handleCancel(appt.id)}>Cancel</button>
                <button onClick={() => handleDownloadPdf(appt.id)}>View Prescription</button>
              </>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
}