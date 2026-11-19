package com.example.hospital_manager.config;

import com.example.hospital_manager.model.AppUser;
import com.example.hospital_manager.model.Patient;
import com.example.hospital_manager.model.Role;
import com.example.hospital_manager.repository.PatientRepository;
import com.example.hospital_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.save(new AppUser(
                        "admin",
                        passwordEncoder.encode("admin123"),
                        "System Administrator",
                        Role.ADMIN
                ));
                userRepository.save(new AppUser(
                        "doctor1",
                        passwordEncoder.encode("doctor123"),
                        "Dr. Kavinda Perera",
                        Role.DOCTOR
                ));
                userRepository.save(new AppUser(
                        "nurse1",
                        passwordEncoder.encode("nurse123"),
                        "Nimali Silva",
                        Role.NURSE
                ));
                userRepository.save(new AppUser(
                        "reception1",
                        passwordEncoder.encode("reception123"),
                        "Saman Kumara",
                        Role.RECEPTIONIST
                ));
            }

            if (patientRepository.count() == 0) {
                Patient p1 = new Patient();
                p1.setPatientCode("P001");
                p1.setFullName("Kasun Fernando");
                p1.setEmail("kasun@example.com");
                p1.setPhone("0771112233");
                patientRepository.save(p1);

                Patient p2 = new Patient();
                p2.setPatientCode("P002");
                p2.setFullName("Tharushi Wijesinghe");
                p2.setEmail("tharushi@example.com");
                p2.setPhone("0714445566");
                patientRepository.save(p2);
            }
        };
    }
}