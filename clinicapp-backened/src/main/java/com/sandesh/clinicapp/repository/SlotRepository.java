package com.sandesh.clinicapp.repository;

import com.sandesh.clinicapp.model.Slot;
import com.sandesh.clinicapp.model.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findByDoctorIdAndStatusAndStartTimeAfter(Long doctorId, SlotStatus status, LocalDateTime after);
}