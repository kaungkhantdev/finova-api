package com.financial.api.dto.mapper;

import com.financial.api.dto.response.UserResponse;
import com.financial.api.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setUsername(user.getUsername());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setBio(user.getBio());
        return response;
    }
}
