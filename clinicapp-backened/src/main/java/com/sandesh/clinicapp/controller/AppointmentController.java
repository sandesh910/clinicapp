package com.sandesh.clinicapp.controller;

import com.sandesh.clinicapp.dto.AppointmentResponse;
import com.sandesh.clinicapp.dto.BookingRequest;
import com.sandesh.clinicapp.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.sandesh.clinicapp.dto.PrescriptionRequest;
import com.sandesh.clinicapp.dto.PrescriptionResponse;
import com.sandesh.clinicapp.service.PrescriptionService;
import java.util.List;


@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final PrescriptionService prescriptionService;
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
    @PostMapping("/{id}/prescription")
    @PreAuthorize("hasRole('DOCTOR')")
    public PrescriptionResponse createPrescription(Authentication authentication, @PathVariable Long id,
                                                   @RequestBody PrescriptionRequest request) {
        return prescriptionService.createPrescription(authentication.getName(), id, request);
    }

    @GetMapping("/{id}/prescription/pdf")
    public ResponseEntity<byte[]> downloadPrescriptionPdf(Authentication authentication, @PathVariable Long id) {
        byte[] pdfBytes = prescriptionService.generatePdf(authentication.getName(), id);

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "inline; filename=prescription-" + id + ".pdf")
                .body(pdfBytes);
    }
}