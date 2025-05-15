package com.ProdeMaster.PredictionService.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "predictions")
public class PredictionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identificador del usuario que realiza la predicción
    @Column(nullable = false)
    private Long userId;

    // Identificador del partido
    @Column(nullable = false)
    private String matchId;

    // Predicción del resultado para el equipo 1
    @Column(nullable = false)
    private Integer scoreTeam1;

    // Predicción del resultado para el equipo 2
    @Column(nullable = false)
    private Integer scoreTeam2;

    // Fecha y hora de creación de la predicción
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Fecha límite para realizar o modificar la predicción
    @Column(nullable = false)
    private LocalDateTime deadline;

    // Resultado real (opcional, para actualizar luego)
    private Integer resultTeam1;
    private Integer resultTeam2;

    public PredictionModel(Long id, Long userId, String matchId, Integer scoreTeam1, Integer scoreTeam2, LocalDateTime createdAt, LocalDateTime deadline, Integer resultTeam1, Integer resultTeam2) {
        this.id = id;
        this.userId = userId;
        this.matchId = matchId;
        this.scoreTeam1 = scoreTeam1;
        this.scoreTeam2 = scoreTeam2;
        this.createdAt = createdAt;
        this.deadline = deadline;
        this.resultTeam1 = resultTeam1;
        this.resultTeam2 = resultTeam2;
    }

    public PredictionModel(Long userId, String matchId, Integer scoreTeam1, Integer scoreTeam2, LocalDateTime createdAt, LocalDateTime deadline) {
        this.userId = userId;
        this.matchId = matchId;
        this.scoreTeam1 = scoreTeam1;
        this.scoreTeam2 = scoreTeam2;
        this.createdAt = createdAt;
        this.deadline = deadline;
    }

    public PredictionModel() {
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getMatchId() {
        return matchId;
    }

    public Integer getScoreTeam1() {
        return scoreTeam1;
    }

    public Integer getScoreTeam2() {
        return scoreTeam2;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public Integer getResultTeam1() {
        return resultTeam1;
    }

    public Integer getResultTeam2() {
        return resultTeam2;
    }

    public void setResultTeam2(Integer resultTeam2) {
        this.resultTeam2 = resultTeam2;
    }

    public void setResultTeam1(Integer resultTeam1) {
        this.resultTeam1 = resultTeam1;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setScoreTeam2(Integer scoreTeam2) {
        this.scoreTeam2 = scoreTeam2;
    }

    public void setScoreTeam1(Integer scoreTeam1) {
        this.scoreTeam1 = scoreTeam1;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}