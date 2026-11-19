package com.example.hospital_manager.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentRequest(
        Long patientId,
        @NotNull(message = "Doctor ID is required") Long doctorId,
        @NotNull(message = "Appointment time is required")
        @Future(message = "Appointment time must be in the future") LocalDateTime appointmentTime,
        String reason
) {}