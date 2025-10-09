package com.financial.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request object for creating a new category")
public class CategoryCreateRequest {
    @Schema(
            description = "Name of the category",
            example = "Food",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 100
    )
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    @Schema(
            description = "Category Description",
            example = "USD",
            maxLength = 10
    )
    @Size(max = 250, message = "Description must not exceed 250 characters")
    private String description;
}
