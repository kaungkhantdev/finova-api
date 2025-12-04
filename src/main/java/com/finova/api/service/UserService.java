package com.finova.api.service;


import com.finova.api.dto.response.UserResponse;

public interface UserService {
    UserResponse getUserById(Long id);
    UserResponse getUserByEmail(String email);
    UserResponse getCurrentUserProfile();
}
