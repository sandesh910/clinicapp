package com.sandesh.clinicapp.service;

import com.sandesh.clinicapp.dto.AppointmentResponse;
import com.sandesh.clinicapp.dto.BookingRequest;
import com.sandesh.clinicapp.exception.SlotConflictException;
import com.sandesh.clinicapp.model.*;
import com.sandesh.clinicapp.repository.AppointmentRepository;
import com.sandesh.clinicapp.repository.SlotRepository;
import com.sandesh.clinicapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final SlotRepository slotRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @Transactional
    public AppointmentResponse bookAppointment(String patientEmail, BookingRequest request) {
        Slot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new SlotConflictException("Slot not found"));

        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new SlotConflictException("Slot is already booked");
        }

        User patient = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        slot.setStatus(SlotStatus.BOOKED);

        try {
            slotRepository.saveAndFlush(slot); // forces the UPDATE now, inside this transaction
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new SlotConflictException("Slot was just booked by someone else");
        }

        Appointment appointment = new Appointment();
        appointment.setSlot(slot);
        appointment.setPatient(patient);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setBookedAt(LocalDateTime.now());

        appointmentRepository.save(appointment);

        return AppointmentResponse.from(appointment);
    }
}