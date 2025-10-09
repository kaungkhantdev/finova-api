package com.financial.api.service.impl;

import com.financial.api.dto.mapper.CategoryMapper;
import com.financial.api.dto.request.CategoryCreateRequest;
import com.financial.api.dto.request.CategoryUpdateRequest;
import com.financial.api.dto.response.CategoryResponse;
import com.financial.api.entity.*;
import com.financial.api.entity.Category;
import com.financial.api.entity.Category;
import com.financial.api.repository.CategoryRepository;
import com.financial.api.service.CategoryService;
import com.financial.api.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final AuthenticationUtil authenticationUtil;

    @Override
    public Page<CategoryResponse> getAll(Pageable pageable) {
        User currentUser = getCurrentUser();

        // Get system + user's own
        Page<Category> categories = categoryRepository
                .findByUserOrIsSystemTrueAndIsDeletedFalse(currentUser, pageable);

        return categories.map(categoryMapper::toResponse);
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
