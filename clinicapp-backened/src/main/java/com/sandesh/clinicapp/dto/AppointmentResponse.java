package com.sandesh.clinicapp.dto;

import com.sandesh.clinicapp.model.Appointment;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AppointmentResponse {
    private Long id;
    private Long slotId;
    private String doctorName;
    private String patientName;
    private LocalDateTime startTime;
    private String status;

    public static AppointmentResponse from(Appointment appt) {
        AppointmentResponse dto = new AppointmentResponse();
        dto.setId(appt.getId());
        dto.setSlotId(appt.getSlot().getId());
        dto.setDoctorName(appt.getSlot().getDoctor().getName());
        dto.setPatientName(appt.getPatient().getName());
        dto.setStartTime(appt.getSlot().getStartTime());
        dto.setStatus(appt.getStatus().name());
        return dto;
    }
}