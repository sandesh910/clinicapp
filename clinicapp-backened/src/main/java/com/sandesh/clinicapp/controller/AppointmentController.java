package com.sandesh.clinicapp.controller;

import com.sandesh.clinicapp.dto.AppointmentResponse;
import com.sandesh.clinicapp.dto.BookingRequest;
import com.sandesh.clinicapp.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
}