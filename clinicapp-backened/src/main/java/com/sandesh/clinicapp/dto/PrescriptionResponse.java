package com.sandesh.clinicapp.dto;

import com.sandesh.clinicapp.model.Prescription;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PrescriptionResponse {
    private Long id;
    private Long appointmentId;
    private String doctorName;
    private String patientName;
    private String medicines;
    private String notes;
    private LocalDateTime createdAt;

    public static PrescriptionResponse from(Prescription p) {
        PrescriptionResponse dto = new PrescriptionResponse();
        dto.setId(p.getId());
        dto.setAppointmentId(p.getAppointment().getId());
        dto.setDoctorName(p.getAppointment().getSlot().getDoctor().getName());
        dto.setPatientName(p.getAppointment().getPatient().getName());
        dto.setMedicines(p.getMedicines());
        dto.setNotes(p.getNotes());
        dto.setCreatedAt(p.getCreatedAt());
        return dto;
    }
}