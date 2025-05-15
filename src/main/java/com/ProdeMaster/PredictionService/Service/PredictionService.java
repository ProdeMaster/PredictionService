package com.ProdeMaster.PredictionService.Service;

import com.ProdeMaster.PredictionService.Model.PredictionModel;
import com.ProdeMaster.PredictionService.Repository.PredictionRepository;
import com.ProdeMaster.PredictionService.Dto.PredictionDto;
import com.ProdeMaster.PredictionService.Service.Client.Dto.DeadlineDto;
import com.ProdeMaster.PredictionService.Service.Client.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PredictionService {

    @Autowired
    private PredictionRepository predictionRepository;

    /**
     * Registra una nueva predicción. Valida si se puede registrar basándose en el deadline.
     */
    public PredictionModel createPrediction(PredictionDto prediction) {
        // Obtengo la deadline de matchService
        DeadlineDto matchDeadLine = MatchService.getDeadLine(prediction.getMatchId());

        // Validar que la predicción se realiza antes del deadline
        if (LocalDateTime.now().isAfter(matchDeadLine.getDeadLine())) {
            throw new IllegalArgumentException("El tiempo para realizar la predicción ha finalizado.");
        }

        PredictionModel newPrediction = new PredictionModel (prediction.getUserId(), prediction.getMatchId(), prediction.getScoreTeam1(), prediction.getScoreTeam2(), LocalDateTime.now(), matchDeadLine.getDeadLine());
        return predictionRepository.save(newPrediction);
    }

    /**
     * Actualiza el resultado real de una predicción.
     */
    public PredictionModel updatePredictionResult(String userId, String matchId, Integer resultTeam1, Integer resultTeam2) {
        PredictionModel prediction = predictionRepository.findByUserIdAndMatchId(userId, matchId);
        if (prediction == null) {
            throw new IllegalArgumentException("Predicción no encontrada para el usuario y partido especificado.");
        }
        prediction.setResultTeam1(resultTeam1);
        prediction.setResultTeam2(resultTeam2);
        return predictionRepository.save(prediction);
    }

    /**
     * Devuelve todas las predicciones de un usuario.
     */
    public List<PredictionModel> getPredictionsByUser(String userId) {
        return predictionRepository.findByUserId(userId);
    }
}
