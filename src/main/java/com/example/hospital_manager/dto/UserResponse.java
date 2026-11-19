package com.example.hospital_manager.dto;

import com.example.hospital_manager.model.AppUser;

public record UserResponse(
        Long id,
        String username,
        String fullName,
        String role
) {
    public static UserResponse from(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole().name()
        );
    }
}