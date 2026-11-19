package com.example.hospital_manager.service;

import com.example.hospital_manager.dto.MedicalRecordRequest;
import com.example.hospital_manager.dto.MedicalRecordResponse;
import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.AppUser;
import com.example.hospital_manager.model.MedicalRecord;
import com.example.hospital_manager.model.Patient;
import com.example.hospital_manager.repository.MedicalRecordRepository;
import com.example.hospital_manager.repository.PatientRepository;
import com.example.hospital_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public MedicalRecordResponse createRecord(MedicalRecordRequest request, String doctorUsername) {
        AppUser doctor = userRepository.findByUsername(doctorUsername)
                .orElseThrow(() -> ApiException.notFound("Doctor not found: " + doctorUsername));

        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> ApiException.notFound("Patient not found with id: " + request.patientId()));

        MedicalRecord record = new MedicalRecord();
        record.setPatient(patient);
        record.setDoctor(doctor);
        record.setDiagnosis(request.diagnosis());
        record.setPrescription(request.prescription());
        record.setCreatedAt(LocalDateTime.now());

        return MedicalRecordResponse.from(medicalRecordRepository.save(record));
    }

    public List<MedicalRecordResponse> getRecordsByPatientId(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw ApiException.notFound("Patient not found with id: " + patientId);
        }

        return medicalRecordRepository.findByPatient_Id(patientId).stream()
                .map(MedicalRecordResponse::from)
                .toList();
    }
}