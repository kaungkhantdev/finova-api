package com.financial.api.service;


import com.financial.api.dto.response.UserResponse;

public interface UserService {
    UserResponse getUserById(Long id);
    UserResponse getUserByEmail(String email);
    UserResponse getCurrentUserProfile();
}
