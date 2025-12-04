package com.finova.api.service.impl;

import com.finova.api.dto.mapper.CategoryMapper;
import com.finova.api.dto.request.CategoryCreateRequest;
import com.finova.api.dto.request.CategoryUpdateRequest;
import com.finova.api.dto.response.CategoryResponse;
import com.finova.api.entity.*;
import com.finova.api.entity.Category;
import com.finova.api.entity.User;
import com.finova.api.repository.CategoryRepository;
import com.finova.api.service.CategoryService;
import com.finova.api.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final AuthenticationUtil authenticationUtil;

    @Override
    public Page<CategoryResponse> getAll(Pageable pageable, String s) {
        User currentUser = getCurrentUser();

        // Get system + user's own
        Page<Category> categories = categoryRepository
                .findByUserOrIsSystemTrueAndIsDeletedFalse(currentUser, s, pageable);

        return categories.map(categoryMapper::toResponse);
    }

    @Override
    public List<CategoryResponse> getAllNoPagination() {
        User currentUser = getCurrentUser();
        List<Category> categories = categoryRepository.findAllByUserOrIsSystemTrueAndIsDeletedFalse(currentUser);
        return categories.stream().map(categoryMapper::toResponse).toList();
    }

    @Override
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        User currentUser =  getCurrentUser();

        if (categoryRepository.existsByUserAndNameIgnoreCaseAndIsDeletedFalse(currentUser, request.getName())) {
            throw new IllegalArgumentException("You already have a category with name: " + request.getName());
        }

        Category category = categoryMapper.toEntity(request);
        category.setUser(currentUser);
        category.setIsSystem(false);

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest request) {
        User currentUser = getCurrentUser();

        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Category not found with ID: " + id));

        // Validate user can update this category
        validateUpdatePermission(existing, currentUser);

        if (!existing.getName().equals(request.getName()) &&
                categoryRepository.existsByUserAndNameIgnoreCaseAndIsDeletedFalse(currentUser, request.getName())) {
            throw new IllegalArgumentException("You already have a category with code: " + request.getName());
        }

        // Update fields
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());

        Category updatedCategory = categoryRepository.save(existing);
        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Category not found with ID: " + id));

        if (category.getIsDeleted()) {
            throw new NoSuchElementException("Category not found with ID: " + id);
        }

        return categoryMapper.toResponse(category);
    }

    @Override
    public void deleteCategory(Long id) {
        User currentUser = getCurrentUser();

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Category not found with ID: " + id));

        // Validate user can delete this category
        validateUpdatePermission(category, currentUser);

        // Soft delete
        category.setIsDeleted(true);
        categoryRepository.save(category);
    }

    /**
     * Get the currently authenticated user
     */
    private User getCurrentUser() {
        return authenticationUtil.getCurrentUser();
    }

    /**
     * Validate if the current user can update the given category
     */
    private void validateUpdatePermission(Category category, User currentUser) {
        // Cannot update system currencies
        if (category.getIsSystem()) {
            throw new AccessDeniedException("Cannot modify system currencies");
        }

        // Can only update own currencies
        if (category.getUser() == null || !category.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only update your own currencies");
        }
    }
}
