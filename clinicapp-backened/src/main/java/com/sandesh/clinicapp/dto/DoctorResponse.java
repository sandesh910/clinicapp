package com.sandesh.clinicapp.dto;

import com.sandesh.clinicapp.model.User;
import lombok.Data;

@Data
public class DoctorResponse {
    private Long id;
    private String name;

    public static DoctorResponse from(User user) {
        DoctorResponse dto = new DoctorResponse();
        dto.setId(user.getId());
        dto.setName(user.getName());
        return dto;
    }
}