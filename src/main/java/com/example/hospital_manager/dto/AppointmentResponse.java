package com.example.hospital_manager.dto;

import com.example.hospital_manager.model.Appoinment;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long patientId,
        String patientCode,
        String patientName,
        Long doctorId,
        String doctorName,
        LocalDateTime appointmentTime,
        String status,
        String reason
) {
    public static AppointmentResponse from(Appoinment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getPatient().getId(),
                a.getPatient().getPatientCode(),
                a.getPatient().getFullName(),
                a.getDoctor().getId(),
                a.getDoctor().getFullName(),
                a.getAppointmentTime(),
                a.getStatus().name(),
                a.getReason()
        );
    }
}