package com.financial.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "currencies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Currency extends BaseEntity{
    @Column(nullable = false, length = 100)
    private String currency;

    @Column(name = "currency_code", nullable = false, length = 10)
    private String code; // "USD", "EUR", "GBP"

    @Column(name = "symbol", length = 10)
    private String symbol; // "$", "€", "£"
}
