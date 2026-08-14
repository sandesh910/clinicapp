package com.sandesh.clinicapp.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.sandesh.clinicapp.dto.PrescriptionRequest;
import com.sandesh.clinicapp.dto.PrescriptionResponse;
import com.sandesh.clinicapp.model.Appointment;
import com.sandesh.clinicapp.model.Prescription;
import com.sandesh.clinicapp.repository.AppointmentRepository;
import com.sandesh.clinicapp.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;

    public PrescriptionResponse createPrescription(String doctorEmail, Long appointmentId, PrescriptionRequest request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));


        if (!appointment.getSlot().getDoctor().getEmail().equals(doctorEmail)) {
            throw new AccessDeniedException("You can only write prescriptions for your own appointments");
        }

        Prescription prescription = new Prescription();
        prescription.setAppointment(appointment);
        prescription.setMedicines(request.getMedicines());
        prescription.setNotes(request.getNotes());
        prescription.setCreatedAt(LocalDateTime.now());

        prescriptionRepository.save(prescription);

        return PrescriptionResponse.from(prescription);
    }

    public byte[] generatePdf(String requesterEmail, Long appointmentId) {
        Prescription prescription = prescriptionRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        Appointment appointment = prescription.getAppointment();


        boolean isDoctor = appointment.getSlot().getDoctor().getEmail().equals(requesterEmail);
        boolean isPatient = appointment.getPatient().getEmail().equals(requesterEmail);
        if (!isDoctor && !isPatient) {
            throw new AccessDeniedException("You are not authorized to view this prescription");
        }

        try {
            Document document = new Document();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font labelFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL);

            document.add(new Paragraph("ClinicApp Prescription", titleFont));
            document.add(new Paragraph(" ")); // spacer

            document.add(new Paragraph("Doctor: " + appointment.getSlot().getDoctor().getName(), labelFont));
            document.add(new Paragraph("Patient: " + appointment.getPatient().getName(), labelFont));
            document.add(new Paragraph("Date: " +
                    prescription.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")), labelFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Medicines:", labelFont));
            document.add(new Paragraph(prescription.getMedicines(), normalFont));
            document.add(new Paragraph(" "));

            if (prescription.getNotes() != null && !prescription.getNotes().isBlank()) {
                document.add(new Paragraph("Notes:", labelFont));
                document.add(new Paragraph(prescription.getNotes(), normalFont));
            }

            document.close();
            return outputStream.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}