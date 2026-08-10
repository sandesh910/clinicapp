package com.sandesh.clinicapp.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class GenerateSlotsRequest {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int slotDurationMinutes;
}