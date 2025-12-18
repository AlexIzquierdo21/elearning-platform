package com.elearning.elearning_platform.shared.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object representing a monetary amount in a specific currency.
 *
 * A @code Money instance is immutable and enforces domain rules such as:
 *
 *   Amount cannot be {@code null}
 *   Amount cannot be negative
 *   Currency must be defined and non-blank
 *
 * Two {@code Money} objects are considered equal if both their amount and
 * currency are equal.
 */
public class Money {

    private final BigDecimal amount;
    private final String currency;

    private Money(BigDecimal amount, String currency) {
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

    /**
     * Creates a new {@code Money} instance with the given amount and currency.
     *
     * @param amount   monetary amount, must be non-null and non-negative
     * @param currency currency code (e.g. "EUR", "USD"), must be non-null and non-blank
     * @return a new {@code Money} value object
     */
    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    /**
     * @return the monetary amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @return the currency code in upper case
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Adds another {@code Money} to this one.
     *
     * @param other money to add, must have the same currency
     * @return a new {@code Money} instance with the summed amount
     * @throws IllegalArgumentException if currencies are different
     */
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }

        BigDecimal suma = this.amount.add(other.amount);
        return Money.of(suma, currency);
    }

    /**
     * Multiplies this {@code Money} by a given factor.
     *
     * @param factor multiplication factor, must be non-null and greater than zero
     * @return a new {@code Money} instance with the multiplied amount
     * @throws IllegalArgumentException if the factor is null or not positive
     */
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
        if (!(o instanceof Money money)) return false;
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
