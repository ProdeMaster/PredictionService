package com.ProdeMaster.PredictionService.Repository;

import com.ProdeMaster.PredictionService.Model.PredictionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PredictionRepository extends JpaRepository<PredictionModel, Long>  {

    // Obtener todas las predicciones de un usuario
    List<PredictionModel> findByUserId(String userId);

    // Buscar predicción de un usuario para un partido específico
    PredictionModel findByUserIdAndMatchId(String userId, String matchId);
}
