import { useAuth } from '../context/AuthContext';

export default function PatientDashboard() {
  const { user, logout } = useAuth();
  return (
    <div>
      <h2>Patient Dashboard</h2>
      <p>Logged in as: {user.email}</p>
      <button onClick={logout}>Logout</button>
    </div>
  );
}