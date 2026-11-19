package com.example.hospital_manager.service;

import com.example.hospital_manager.dto.PatientRequest;
import com.example.hospital_manager.dto.PatientResponse;
import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.AppUser;
import com.example.hospital_manager.model.Patient;
import com.example.hospital_manager.repository.AppointmentRepository;
import com.example.hospital_manager.repository.MedicalRecordRepository;
import com.example.hospital_manager.repository.PatientRepository;
import com.example.hospital_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public List<PatientResponse> getAllPatients(String name) {
        List<Patient> patients = (name == null || name.isBlank())
                ? patientRepository.findAll()
                : patientRepository.findByFullNameContainingIgnoreCase(name.trim());
        return patients.stream()
                .map(PatientResponse::from)
                .toList();
    }

    public PatientResponse getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Patient not found with id: " + id));
        return PatientResponse.from(patient);
    }

    public PatientResponse createPatient(PatientRequest request) {
        if (patientRepository.existsByPatientCode(request.patientCode())) {
            throw ApiException.conflict("Patient code already exists: " + request.patientCode());
        }

        Patient patient = new Patient();
        patient.setPatientCode(request.patientCode());
        patient.setFullName(request.fullName());
        patient.setEmail(request.email());
        patient.setPhone(request.phone());

        if (request.username() != null && !request.username().isBlank()) {
            AppUser linkedUser = userRepository.findByUsername(request.username().trim())
                    .orElseThrow(() -> ApiException.notFound("User not found: " + request.username()));
            patient.setUser(linkedUser);
        }

        return PatientResponse.from(patientRepository.save(patient));
    }

    public PatientResponse updatePatient(Long id, PatientRequest request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Patient not found with id: " + id));

        if (!patient.getPatientCode().equals(request.patientCode())
                && patientRepository.existsByPatientCode(request.patientCode())) {
            throw ApiException.conflict("Patient code already exists: " + request.patientCode());
        }

        patient.setPatientCode(request.patientCode());
        patient.setFullName(request.fullName());
        patient.setEmail(request.email());
        patient.setPhone(request.phone());

        return PatientResponse.from(patientRepository.save(patient));
    }

    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Patient not found with id: " + id));

        if (appointmentRepository.existsByPatient_Id(id) || medicalRecordRepository.existsByPatient_Id(id)) {
            throw ApiException.conflict("Cannot delete patient with existing appointments or medical records");
        }

        patientRepository.delete(patient);
    }
}