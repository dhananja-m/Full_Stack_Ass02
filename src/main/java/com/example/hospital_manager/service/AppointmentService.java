package com.example.hospital_manager.service;

import com.example.hospital_manager.dto.AppointmentRequest;
import com.example.hospital_manager.dto.AppointmentResponse;
import com.example.hospital_manager.dto.StatusRequest;
import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.AppUser;
import com.example.hospital_manager.model.Appoinment;
import com.example.hospital_manager.model.AppointmentStatus;
import com.example.hospital_manager.model.Patient;
import com.example.hospital_manager.model.Role;
import com.example.hospital_manager.repository.AppointmentRepository;
import com.example.hospital_manager.repository.PatientRepository;
import com.example.hospital_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public AppointmentResponse bookAppointment(AppointmentRequest request, String authenticatedUsername) {
        AppUser caller = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> ApiException.notFound("User not found: " + authenticatedUsername));

        AppUser doctor = userRepository.findById(request.doctorId())
                .orElseThrow(() -> ApiException.notFound("Doctor not found with id: " + request.doctorId()));

        if (doctor.getRole() != Role.DOCTOR) {
            throw ApiException.badRequest("Selected user is not a DOCTOR");
        }

        Patient patient;
        if (caller.getRole() == Role.PATIENT) {
            patient = patientRepository.findByUser_Username(caller.getUsername())
                    .orElseGet(() -> {
                        Patient autoPatient = new Patient();
                        autoPatient.setPatientCode("PAT-" + caller.getId());
                        autoPatient.setFullName(caller.getFullName());
                        autoPatient.setUser(caller);
                        return patientRepository.save(autoPatient);
                    });
        } else {
            if (request.patientId() == null) {
                throw ApiException.badRequest("patientId is required when booking on behalf of a patient");
            }
            patient = patientRepository.findById(request.patientId())
                    .orElseThrow(() -> ApiException.notFound("Patient not found with id: " + request.patientId()));
        }

        Appoinment appointment = new Appoinment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentTime(request.appointmentTime());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setReason(request.reason());

        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    public List<AppointmentResponse> getAppointmentsForUser(String authenticatedUsername) {
        AppUser caller = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> ApiException.notFound("User not found: " + authenticatedUsername));

        List<Appoinment> list;
        if (caller.getRole() == Role.ADMIN) {
            list = appointmentRepository.findAll();
        } else if (caller.getRole() == Role.DOCTOR) {
            list = appointmentRepository.findByDoctor_Id(caller.getId());
        } else if (caller.getRole() == Role.PATIENT) {
            list = patientRepository.findByUser_Username(caller.getUsername())
                    .map(p -> appointmentRepository.findByPatient_Id(p.getId()))
                    .orElse(List.of());
        } else {
            list = appointmentRepository.findAll();
        }

        return list.stream()
                .map(AppointmentResponse::from)
                .toList();
    }

    public AppointmentResponse updateStatus(Long appointmentId, StatusRequest request, String authenticatedUsername) {
        AppUser caller = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> ApiException.notFound("User not found: " + authenticatedUsername));

        Appoinment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> ApiException.notFound("Appointment not found with id: " + appointmentId));

        boolean isAdmin = caller.getRole() == Role.ADMIN;
        boolean isAssignedDoctor = caller.getRole() == Role.DOCTOR
                && appointment.getDoctor().getId().equals(caller.getId());

        if (!isAdmin && !isAssignedDoctor) {
            throw ApiException.forbidden("You do not own this appointment");
        }

        appointment.setStatus(request.status());
        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }
}