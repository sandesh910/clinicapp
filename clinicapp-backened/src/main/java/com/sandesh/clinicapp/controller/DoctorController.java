package com.sandesh.clinicapp.controller;

import com.sandesh.clinicapp.dto.AppointmentResponse;
import com.sandesh.clinicapp.dto.DoctorResponse;
import com.sandesh.clinicapp.dto.GenerateSlotsRequest;
import com.sandesh.clinicapp.dto.SlotResponse;
import com.sandesh.clinicapp.service.AppointmentService;
import com.sandesh.clinicapp.service.SlotService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class DoctorController {

    private final SlotService slotService;
    private final AppointmentService appointmentService;

    @PostMapping("/{id}/slots")
    @PreAuthorize("hasRole('DOCTOR')")
    public List<SlotResponse> generateSlots(@PathVariable Long id, @RequestBody GenerateSlotsRequest request) {
        return slotService.generateSlots(id, request);
    }

    @GetMapping("/{id}/slots")
    public List<SlotResponse> getSlots(@PathVariable Long id) {
        return slotService.getAvailableSlots(id);
    }

    @GetMapping("/{id}/appointments")
    @PreAuthorize("hasRole('DOCTOR')")
    public List<AppointmentResponse> getDoctorAppointments(@PathVariable Long id) {
        return appointmentService.getDoctorAppointments(id);
    }
    @GetMapping
    public List<DoctorResponse> getAllDoctors() {
        return slotService.getAllDoctors();
    }
}