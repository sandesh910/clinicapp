package com.sandesh.clinicapp.dto;

import com.sandesh.clinicapp.model.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role;
}