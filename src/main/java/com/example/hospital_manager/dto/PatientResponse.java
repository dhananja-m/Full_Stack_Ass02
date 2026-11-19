package com.example.hospital_manager.dto;

import com.example.hospital_manager.model.Patient;

public record PatientResponse(
        Long id,
        String patientCode,
        String fullName,
        String email,
        String phone
) {
    public static PatientResponse from(Patient p) {
        return new PatientResponse(
                p.getId(),
                p.getPatientCode(),
                p.getFullName(),
                p.getEmail(),
                p.getPhone()
        );
    }
}