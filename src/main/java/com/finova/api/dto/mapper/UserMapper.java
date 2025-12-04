package com.finova.api.dto.mapper;

import com.finova.api.dto.response.UserResponse;
import com.finova.api.entity.User;
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
        response.setCurrency(user.getCurrency().getCurrency());
        response.setCurrencySymbol(user.getCurrency().getSymbol());
        response.setBio(user.getBio());
        return response;
    }
}
