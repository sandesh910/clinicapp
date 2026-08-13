package com.sandesh.clinicapp.repository;

import com.sandesh.clinicapp.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findBySlotDoctorId(Long doctorId);
}