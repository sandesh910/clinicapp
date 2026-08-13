package com.sandesh.clinicapp.controller;

import com.sandesh.clinicapp.dto.AppointmentResponse;
import com.sandesh.clinicapp.dto.BookingRequest;
import com.sandesh.clinicapp.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public AppointmentResponse book(Authentication authentication, @RequestBody BookingRequest request) {
        String patientEmail = authentication.getName();
        return appointmentService.bookAppointment(patientEmail, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Void> cancel(Authentication authentication, @PathVariable Long id) {
        String patientEmail = authentication.getName();
        appointmentService.cancelAppointment(patientEmail, id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    @PreAuthorize("hasRole('PATIENT')")
    public List<AppointmentResponse> getMyAppointments(Authentication authentication) {
        return appointmentService.getPatientAppointments(authentication.getName());
    }
}