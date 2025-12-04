package com.finova.api.service;


import com.finova.api.dto.request.CategoryCreateRequest;
import com.finova.api.dto.request.CategoryUpdateRequest;
import com.finova.api.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    Page<CategoryResponse> getAll(Pageable pageable, String s);
    List<CategoryResponse> getAllNoPagination();
    CategoryResponse createCategory(CategoryCreateRequest request);
    CategoryResponse updateCategory(Long id, CategoryUpdateRequest request);
    CategoryResponse getCategoryById(Long id);
    void deleteCategory(Long id);
}
