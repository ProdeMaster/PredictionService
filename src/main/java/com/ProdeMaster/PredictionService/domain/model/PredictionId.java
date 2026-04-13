package com.ProdeMaster.PredictionService.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class PredictionId {
    private final String value;

    public PredictionId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PredictionId cannot be null or blank");
        }
        this.value = value;
    }

    public static PredictionId generate() {
        return new PredictionId(UUID.randomUUID().toString());
    }

    public static PredictionId of(String value) {
        return new PredictionId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PredictionId that = (PredictionId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}