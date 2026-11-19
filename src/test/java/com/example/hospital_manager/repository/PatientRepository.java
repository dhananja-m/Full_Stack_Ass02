package com.example.hospital_manager.repository;

import com.example.hospital_manager.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByFullNameContainingIgnoreCase(String name);
    Optional<Patient> findByPatientCode(String patientCode);
    Optional<Patient> findByUser_Username(String username);
    boolean existsByPatientCode(String patientCode);
}