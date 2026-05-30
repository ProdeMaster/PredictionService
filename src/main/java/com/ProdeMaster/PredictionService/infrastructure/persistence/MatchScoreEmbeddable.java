package com.ProdeMaster.PredictionService.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class MatchScoreEmbeddable {

    @Column(name = "home_team_goals", nullable = false)
    private int homeTeamGoals;

    @Column(name = "away_team_goals", nullable = false)
    private int awayTeamGoals;

    protected MatchScoreEmbeddable() {
    }

    public MatchScoreEmbeddable(int homeTeamGoals, int awayTeamGoals) {
        this.homeTeamGoals = homeTeamGoals;
        this.awayTeamGoals = awayTeamGoals;
    }

    public int getHomeTeamGoals() { return homeTeamGoals; }
    public void setHomeTeamGoals(int homeTeamGoals) { this.homeTeamGoals = homeTeamGoals; }

    public int getAwayTeamGoals() { return awayTeamGoals; }
    public void setAwayTeamGoals(int awayTeamGoals) { this.awayTeamGoals = awayTeamGoals; }
}
