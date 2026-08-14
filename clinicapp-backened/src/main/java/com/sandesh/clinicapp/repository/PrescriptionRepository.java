package com.sandesh.clinicapp.repository;

import com.sandesh.clinicapp.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    Optional<Prescription> findByAppointmentId(Long appointmentId);
}