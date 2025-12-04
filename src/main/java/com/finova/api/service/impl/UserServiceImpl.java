package com.finova.api.service.impl;

import com.finova.api.dto.mapper.UserMapper;
import com.finova.api.dto.response.UserResponse;
import com.finova.api.entity.User;
import com.finova.api.repository.UserRepository;
import com.finova.api.service.UserService;
import com.finova.api.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationUtil authenticationUtil;


    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getCurrentUserProfile() {
        return userMapper.toResponse(getCurrentUser());
    }

    /**
     * Get the currently authenticated user
     */
    private User getCurrentUser() {
        return authenticationUtil.getCurrentUser();
    }
}
