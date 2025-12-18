package com.elearning.elearning_platform.shared.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

public class Money {

    private final BigDecimal amount;
    private final String currency;

    private Money (BigDecimal amount, String currency) {
        if (amount == null) {
            throw new IllegalArgumentException("La cantidad no puede ser null");
        }

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }

        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("La moneda no puede ser null");
        }

        this.amount = amount;
        this.currency = currency.toUpperCase();
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }

       BigDecimal suma = this.amount.add(other.amount);
       return Money.of(suma, currency);
    }

    public Money multiply(BigDecimal factor) {
        if (factor == null) {
            throw new IllegalArgumentException("Multiplication factor cannot be null");
        }
        if (factor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Factor cannot be negative");
        }

        BigDecimal multiplied = this.amount.multiply(factor);
        return Money.of(multiplied, currency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(!(o instanceof Money money)) return false;
        return Objects.equals(amount, money.amount) &&
                Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}
