package com.ProdeMaster.PredictionService.domain.model;

import java.util.Objects;

/**
 * Value Object that represents the exact predicted score of a football match.
 * Captures the number of goals scored by the home team and away team
 * independently.
 *
 * <p>
 * This is the core prediction data in ProdeMaster: a user predicts the exact
 * scoreline (e.g., Home 3 - Away 2), not merely the outcome (win/draw/loss).
 * The match outcome can always be derived from this score via
 * {@link #deriveOutcome()}.
 *
 * <p>
 * Invariants enforced at construction time:
 * <ul>
 * <li>Neither {@code homeTeamGoals} nor {@code awayTeamGoals} may be
 * negative.</li>
 * <li>A maximum of {@value #MAX_GOALS_PER_TEAM} goals per team is accepted as a
 * reasonable upper bound to guard against data-entry mistakes.</li>
 * </ul>
 */
public final class MatchScore {

    private final int homeTeamGoals;
    private final int awayTeamGoals;

    public static final int MAX_GOALS_PER_TEAM = 30;

    private MatchScore(int homeTeamGoals, int awayTeamGoals) {
        validate(homeTeamGoals, awayTeamGoals);
        this.homeTeamGoals = homeTeamGoals;
        this.awayTeamGoals = awayTeamGoals;
    }

    public static MatchScore of(int homeTeamGoals, int awayTeamGoals) {
        return new MatchScore(homeTeamGoals, awayTeamGoals);
    }

    public static MatchScore reconstitute(int homeTeamGoals, int awayTeamGoals) {
        return new MatchScore(homeTeamGoals, awayTeamGoals);
    }

    public int getHomeTeamGoals() {
        return homeTeamGoals;
    }

    public int getAwayTeamGoals() {
        return awayTeamGoals;
    }

    public MatchOutcome deriveOutcome() {
        if (homeTeamGoals > awayTeamGoals)
            return MatchOutcome.HOME_WIN;
        if (awayTeamGoals > homeTeamGoals)
            return MatchOutcome.AWAY_WIN;
        return MatchOutcome.DRAW;
    }

    private static void validate(int homeTeamGoals, int awayTeamGoals) {
        if (homeTeamGoals < 0 || awayTeamGoals < 0) {
            throw new IllegalArgumentException(
                    "Home team goals, or away team, cannot be negative. Home goals: " + homeTeamGoals
                            + ", Away goals: " + awayTeamGoals);
        }
        if (homeTeamGoals > MAX_GOALS_PER_TEAM || awayTeamGoals > MAX_GOALS_PER_TEAM) {
            throw new IllegalArgumentException(
                    "Maximum goals per team: " + MAX_GOALS_PER_TEAM + ". Home goals: " + homeTeamGoals
                            + ", Away goals: " + awayTeamGoals);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MatchScore that = (MatchScore) o;
        return homeTeamGoals == that.homeTeamGoals && awayTeamGoals == that.awayTeamGoals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(homeTeamGoals, awayTeamGoals);
    }

    @Override
    public String toString() {
        return homeTeamGoals + " - " + awayTeamGoals;
    }
}
