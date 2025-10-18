package com.financial.api.controller.api.v1;

import com.financial.api.dto.request.CategoryCreateRequest;
import com.financial.api.dto.request.CategoryUpdateRequest;
import com.financial.api.dto.response.CategoryResponse;
import com.financial.api.service.CategoryService;
import com.financial.api.util.ApiPaginationMetadata;
import com.financial.api.util.AppApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.financial.api.config.OpenApiConfig.BEARER_AUTH;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
@Tag(name = "Category Management", description = "APIs for managing categories (supports both Cookie and Bearer token authentication)")
@SecurityRequirement(name = BEARER_AUTH)
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(
            summary = "Get all categories",
            description = "Retrieve paginated list of all categories. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<List<CategoryResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryResponse> categories = categoryService.getAll(pageable);
        AppApiResponse<List<CategoryResponse>> response = AppApiResponse.success(
                "Get All Categories Successfully",
                categories.getContent(),
                new ApiPaginationMetadata(
                        categories.getNumber(),
                        categories.getSize(),
                        categories.getTotalPages(),
                        categories.getTotalElements()
                )
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(
            summary = "Create a new category",
            description = "Create a new category in the system. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryCreateRequest request
    ) {
        CategoryResponse createdCategory = categoryService.createCategory(request);
        AppApiResponse<CategoryResponse> response = AppApiResponse.success(
                "Category created successfully",
                createdCategory
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get category by ID",
            description = "Retrieve category details by ID. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        CategoryResponse category = categoryService.getCategoryById(id);
        AppApiResponse<CategoryResponse> response = AppApiResponse.success(category);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update category",
            description = "Update an existing category. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateRequest request) {

        CategoryResponse category = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(AppApiResponse.success("Category updated successfully", category));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete category",
            description = "Delete a category by ID. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(AppApiResponse.success("Category deleted successfully", null));
    }
}
