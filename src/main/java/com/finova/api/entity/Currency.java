package com.finova.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "currencies", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "code"})
})
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // NULL for system currencies

    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = false; // true for default/system currencies
}