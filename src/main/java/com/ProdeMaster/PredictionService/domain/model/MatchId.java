package com.ProdeMaster.PredictionService.domain.model;

import java.util.Objects;

public final class MatchId {
    private final String value;

    public MatchId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("MatchId cannot be null or blank");
        }
        this.value = value;
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
        MatchId matchId = (MatchId) o;
        return Objects.equals(value, matchId.value);
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