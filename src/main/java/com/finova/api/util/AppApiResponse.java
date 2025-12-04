package com.finova.api.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppApiResponse<T> {

    // Use enum for better type safety and predefined statuses
    public enum Status {
        SUCCESS("success"),
        ERROR("error"),
        WARNING("warning");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // Getters (remove setters for immutability)
    private String status;
    private String message;
    private T data;
    private Object metadata;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    // Private constructor to enforce use of factory methods
    private AppApiResponse(String status, String message, T data, Object metadata) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.metadata = metadata;
        this.timestamp = LocalDateTime.now();
    }

    // Enhanced factory methods with better overloading
    public static <T> AppApiResponse<T> success(T data) {
        return new AppApiResponse<>(Status.SUCCESS.getValue(), "Operation completed successfully", data, null);
    }

    public static <T> AppApiResponse<T> success(String message, T data) {
        return new AppApiResponse<>(Status.SUCCESS.getValue(), message, data, null);
    }

    public static <T> AppApiResponse<T> success(String message, T data, Object metadata) {
        return new AppApiResponse<>(Status.SUCCESS.getValue(), message, data, metadata);
    }

    public static <T> AppApiResponse<T> error(String message) {
        return new AppApiResponse<>(Status.ERROR.getValue(), message, null, null);
    }

    public static <T> AppApiResponse<T> error(String message, T data) {
        return new AppApiResponse<>(Status.ERROR.getValue(), message, data, null);
    }

    public static <T> AppApiResponse<T> error(String message, T data, Object metadata) {
        return new AppApiResponse<>(Status.ERROR.getValue(), message, data, metadata);
    }

    public static <T> AppApiResponse<T> warning(String message, T data) {
        return new AppApiResponse<>(Status.WARNING.getValue(), message, data, null);
    }

    // Builder pattern for complex construction (optional enhancement)
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private String status;
        private String message;
        private T data;
        private Object metadata;

        public Builder<T> status(Status status) {
            this.status = status.getValue();
            return this;
        }

        public Builder<T> status(String status) {
            this.status = status;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> metadata(Object metadata) {
            this.metadata = metadata;
            return this;
        }

        public AppApiResponse<T> build() {
            return new AppApiResponse<>(
                    Objects.requireNonNull(status, "Status cannot be null"),
                    Objects.requireNonNull(message, "Message cannot be null"),
                    data,
                    metadata
            );
        }
    }

    // Utility methods
    public boolean isSuccess() {
        return Status.SUCCESS.getValue().equals(status);
    }

    public boolean isError() {
        return Status.ERROR.getValue().equals(status);
    }

    public boolean isWarning() {
        return Status.WARNING.getValue().equals(status);
    }

    // Override equals, hashCode, and toString for better debugging
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppApiResponse<?> that = (AppApiResponse<?>) o;
        return Objects.equals(status, that.status) &&
                Objects.equals(message, that.message) &&
                Objects.equals(data, that.data) &&
                Objects.equals(metadata, that.metadata) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, message, data, metadata, timestamp);
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", metadata=" + metadata +
                ", timestamp=" + timestamp +
                '}';
    }
}
