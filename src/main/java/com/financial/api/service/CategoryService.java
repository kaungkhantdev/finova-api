package com.financial.api.service;


import com.financial.api.dto.request.CategoryCreateRequest;
import com.financial.api.dto.request.CategoryUpdateRequest;
import com.financial.api.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<CategoryResponse> getAll(Pageable pageable);
    CategoryResponse createCategory(CategoryCreateRequest request);
    CategoryResponse updateCategory(Long id, CategoryUpdateRequest request);
    CategoryResponse getCategoryById(Long id);
    void deleteCategory(Long id);
}
