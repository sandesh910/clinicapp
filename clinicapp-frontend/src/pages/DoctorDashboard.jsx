import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../api/axiosInstance';

export default function DoctorDashboard() {
  const { user, logout } = useAuth();
  const [slots, setSlots] = useState([]);
  const [appointments, setAppointments] = useState([]);
  const [form, setForm] = useState({ date: '', startTime: '', endTime: '', slotDurationMinutes: 30 });
  const [message, setMessage] = useState('');
  const [prescriptionForm, setPrescriptionForm] = useState({});
  const [prescriptionMessage, setPrescriptionMessage] = useState('');

  const loadSlots = async () => {
    const res = await api.get(`/doctors/${user.userId}/slots`);
    setSlots(res.data);
  };

  const loadAppointments = async () => {
    const res = await api.get(`/doctors/${user.userId}/appointments`);
    setAppointments(res.data);
  };

  useEffect(() => {
    loadSlots();
    loadAppointments();
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleGenerateSlots = async (e) => {
    e.preventDefault();
    setMessage('');
    try {
      await api.post(`/doctors/${user.userId}/slots`, {
        ...form,
        slotDurationMinutes: Number(form.slotDurationMinutes),
      });
      setMessage('Slots generated successfully');
      loadSlots();
    } catch (err) {
      setMessage(err.response?.data?.error || 'Failed to generate slots');
    }
  };

  const handlePrescriptionChange = (appointmentId, field, value) => {
    setPrescriptionForm({
      ...prescriptionForm,
      [appointmentId]: { ...prescriptionForm[appointmentId], [field]: value },
    });
  };

  const handleWritePrescription = async (appointmentId) => {
    setPrescriptionMessage('');
    const data = prescriptionForm[appointmentId] || {};
    try {
      await api.post(`/appointments/${appointmentId}/prescription`, {
        medicines: data.medicines || '',
        notes: data.notes || '',
      });
      setPrescriptionMessage('Prescription saved');
    } catch (err) {
      setPrescriptionMessage(err.response?.data?.error || 'Failed to save prescription');
    }
  };

  return (
    <div>
      <h2>Doctor Dashboard</h2>
      <p>Logged in as: {user.email}</p>
      <button onClick={logout}>Logout</button>

      <hr />

      <h3>Generate Slots</h3>
      <form onSubmit={handleGenerateSlots}>
        <input type="date" name="date" value={form.date} onChange={handleChange} required />
        <input type="time" name="startTime" value={form.startTime} onChange={handleChange} required />
        <input type="time" name="endTime" value={form.endTime} onChange={handleChange} required />
        <input
          type="number"
          name="slotDurationMinutes"
          value={form.slotDurationMinutes}
          onChange={handleChange}
          min="5"
          required
        />
        <button type="submit">Generate</button>
      </form>
      {message && <p>{message}</p>}

      <hr />

      <h3>My Slots</h3>
      <ul>
        {slots.map((slot) => (
          <li key={slot.id}>
            {slot.startTime} – {slot.endTime} ({slot.status})
          </li>
        ))}
      </ul>

      <hr />

      <h3>My Appointments</h3>
      {prescriptionMessage && <p>{prescriptionMessage}</p>}
      <ul>
        {appointments.map((appt) => (
          <li key={appt.id} style={{ marginBottom: '15px' }}>
            {appt.startTime} — Patient: {appt.patientName} ({appt.status})
            {appt.status === 'CONFIRMED' && (
              <div>
                <textarea
                  placeholder="Medicines"
                  value={prescriptionForm[appt.id]?.medicines || ''}
                  onChange={(e) => handlePrescriptionChange(appt.id, 'medicines', e.target.value)}
                />
                <br />
                <textarea
                  placeholder="Notes"
                  value={prescriptionForm[appt.id]?.notes || ''}
                  onChange={(e) => handlePrescriptionChange(appt.id, 'notes', e.target.value)}
                />
                <br />
                <button onClick={() => handleWritePrescription(appt.id)}>Save Prescription</button>
              </div>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
}