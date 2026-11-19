package com.example.hospital_manager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PatientRequest(
        @NotBlank(message = "Patient code is required") String patientCode,
        @NotBlank(message = "Full name is required") String fullName,
        @Email(message = "Valid email is required") String email,
        String phone,
        String username
) {}