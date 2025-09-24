package com.financial.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "otps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Otp extends BaseEntity{
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "otp", nullable = false, length = 100)
    private String otp;
}
