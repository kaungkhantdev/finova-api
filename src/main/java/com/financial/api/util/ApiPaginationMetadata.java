package com.financial.api.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiPaginationMetadata {

    // Getters only (immutable design)
    @JsonProperty("page_size")  // More standard naming
    private final int pageSize;

    @JsonProperty("total_items")
    private final long totalItems;

    @JsonProperty("current_page")
    private final int currentPage;

    @JsonProperty("next_page")
    private final Integer nextPage;

    @JsonProperty("prev_page")
    private final Integer prevPage;

    @JsonProperty("last_page")
    private final int lastPage;

    @JsonProperty("first_page")
    private final int firstPage = 1;  // Always 1 for consistency

    @JsonProperty("has_next")
    private final boolean hasNext;

    @JsonProperty("has_prev")
    private final boolean hasPrev;

    @JsonProperty("total_pages")
    private final int totalPages;

    /**
     * Constructs the pagination metadata.
     *
     * @param currentPageZeroBased zero-indexed current page from Spring Data Page
     * @param pageSize             the page size (number of items per page)
     * @param totalPages           total number of pages
     * @param totalItems           total number of items
     */
    public ApiPaginationMetadata(int currentPageZeroBased, int pageSize, int totalPages, long totalItems) {
        // Convert to one-indexed for client responses
        this.currentPage = currentPageZeroBased + 1;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.lastPage = Math.max(1, totalPages); // Ensure at least page 1
        this.totalItems = totalItems;

        // Calculate navigation
        this.hasNext = this.currentPage < this.lastPage;
        this.hasPrev = this.currentPage > 1;

        // Use null for next/prev when not available (cleaner JSON)
        this.nextPage = this.hasNext ? this.currentPage + 1 : null;
        this.prevPage = this.hasPrev ? this.currentPage - 1 : null;
    }

    /**
     * Convenience constructor that takes Spring Data Page object
     */
    public ApiPaginationMetadata(Page<?> page) {
        this(page.getNumber(), page.getSize(), page.getTotalPages(), page.getTotalElements());
    }

    /**
     * Static factory method for creating from Spring Data Page
     */
    public static ApiPaginationMetadata fromPage(Page<?> page) {
        return new ApiPaginationMetadata(page);
    }

    /**
     * Static factory method for empty results
     */
    public static ApiPaginationMetadata empty(int pageSize) {
        return new ApiPaginationMetadata(0, pageSize, 1, 0);
    }

    // Utility methods
    public boolean isEmpty() {
        return totalItems == 0;
    }

    public boolean isFirstPage() {
        return currentPage == 1;
    }

    public boolean isLastPage() {
        return currentPage == lastPage;
    }

    /**
     * Get page range for pagination display (e.g., "Showing 1-10 of 100")
     */
    @JsonProperty("display_range")
    public String getDisplayRange() {
        if (totalItems == 0) {
            return "0-0 of 0";
        }

        long start = (long) (currentPage - 1) * pageSize + 1;
        long end = Math.min(start + pageSize - 1, totalItems);

        return String.format("%d-%d of %d", start, end, totalItems);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiPaginationMetadata that = (ApiPaginationMetadata) o;
        return pageSize == that.pageSize &&
                totalItems == that.totalItems &&
                currentPage == that.currentPage &&
                lastPage == that.lastPage &&
                firstPage == that.firstPage &&
                hasNext == that.hasNext &&
                hasPrev == that.hasPrev &&
                totalPages == that.totalPages &&
                Objects.equals(nextPage, that.nextPage) &&
                Objects.equals(prevPage, that.prevPage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageSize, totalItems, currentPage, nextPage, prevPage,
                lastPage, firstPage, hasNext, hasPrev, totalPages);
    }

    @Override
    public String toString() {
        return "ApiPaginationMetadata{" +
                "pageSize=" + pageSize +
                ", totalItems=" + totalItems +
                ", currentPage=" + currentPage +
                ", totalPages=" + totalPages +
                ", hasNext=" + hasNext +
                ", hasPrev=" + hasPrev +
                '}';
    }
}