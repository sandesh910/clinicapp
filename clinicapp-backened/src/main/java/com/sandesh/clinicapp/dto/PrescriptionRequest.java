package com.sandesh.clinicapp.dto;

import lombok.Data;

@Data
public class PrescriptionRequest {
    private String medicines;
    private String notes;
}