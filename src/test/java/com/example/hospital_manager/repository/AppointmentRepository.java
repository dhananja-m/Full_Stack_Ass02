package com.example.hospital_manager.repository;

import com.example.hospital_manager.model.Appoinment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appoinment, Long> {
    List<Appoinment> findByDoctor_Id(Long doctorId);
    List<Appoinment> findByPatient_Id(Long patientId);
    boolean existsByPatient_Id(Long patientId);
}