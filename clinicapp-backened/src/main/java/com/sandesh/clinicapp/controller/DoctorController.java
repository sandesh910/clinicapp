package com.sandesh.clinicapp.controller;

import com.sandesh.clinicapp.dto.GenerateSlotsRequest;
import com.sandesh.clinicapp.dto.SlotResponse;
import com.sandesh.clinicapp.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final SlotService slotService;

    @PostMapping("/{id}/slots")
    @PreAuthorize("hasRole('DOCTOR')")
    public List<SlotResponse> generateSlots(@PathVariable Long id, @RequestBody GenerateSlotsRequest request) {
        return slotService.generateSlots(id, request);
    }

    @GetMapping("/{id}/slots")
    public List<SlotResponse> getSlots(@PathVariable Long id) {
        return slotService.getAvailableSlots(id);
    }
}