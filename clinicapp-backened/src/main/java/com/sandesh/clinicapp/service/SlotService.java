package com.sandesh.clinicapp.service;

import com.sandesh.clinicapp.dto.GenerateSlotsRequest;
import com.sandesh.clinicapp.dto.SlotResponse;
import com.sandesh.clinicapp.model.*;
import com.sandesh.clinicapp.repository.SlotRepository;
import com.sandesh.clinicapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlotService {

    private final SlotRepository slotRepository;
    private final UserRepository userRepository;

    public List<SlotResponse> generateSlots(Long doctorId, GenerateSlotsRequest request) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        List<Slot> generated = new ArrayList<>();

        LocalDateTime current = LocalDateTime.of(request.getDate(), request.getStartTime());
        LocalDateTime end = LocalDateTime.of(request.getDate(), request.getEndTime());

        while (current.plusMinutes(request.getSlotDurationMinutes()).isBefore(end)
                || current.plusMinutes(request.getSlotDurationMinutes()).isEqual(end)) {

            Slot slot = new Slot();
            slot.setDoctor(doctor);
            slot.setStartTime(current);
            slot.setEndTime(current.plusMinutes(request.getSlotDurationMinutes()));
            slot.setStatus(SlotStatus.AVAILABLE);

            generated.add(slot);
            current = current.plusMinutes(request.getSlotDurationMinutes());
        }

        List<Slot> saved = slotRepository.saveAll(generated);
        return saved.stream().map(SlotResponse::from).toList();
    }

    public List<SlotResponse> getAvailableSlots(Long doctorId) {
        return slotRepository.findByDoctorIdAndStatus(doctorId, SlotStatus.AVAILABLE)
                .stream().map(SlotResponse::from).toList();
    }
}