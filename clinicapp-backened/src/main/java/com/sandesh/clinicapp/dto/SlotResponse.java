package com.sandesh.clinicapp.dto;

import com.sandesh.clinicapp.model.Slot;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SlotResponse {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    public static SlotResponse from(Slot slot) {
        SlotResponse dto = new SlotResponse();
        dto.setId(slot.getId());
        dto.setDoctorId(slot.getDoctor().getId());
        dto.setDoctorName(slot.getDoctor().getName());
        dto.setStartTime(slot.getStartTime());
        dto.setEndTime(slot.getEndTime());
        dto.setStatus(slot.getStatus().name());
        return dto;
    }
}