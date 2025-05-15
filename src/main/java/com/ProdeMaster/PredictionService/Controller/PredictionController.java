package com.ProdeMaster.PredictionService.Controller;

import com.ProdeMaster.PredictionService.Dto.PredictionDto;
import com.ProdeMaster.PredictionService.Model.PredictionModel;
import com.ProdeMaster.PredictionService.Service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/predictions")

public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    // Crear una nueva predicción
    @PostMapping
    public ResponseEntity<?> createPrediction(@RequestBody PredictionDto prediction) {
        try {
            PredictionModel createdPrediction = predictionService.createPrediction(prediction);
            return ResponseEntity.ok(createdPrediction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Actualizar el resultado real de una predicción (posiblemente invocado por el Match Service)
    @PutMapping("/{userId}/{matchId}")
    public ResponseEntity<?> updatePredictionResult(@PathVariable String userId,
                                                    @PathVariable String matchId,
                                                    @RequestParam Integer resultTeam1,
                                                    @RequestParam Integer resultTeam2) {
        try {
            PredictionModel updatedPrediction = predictionService.updatePredictionResult(userId, matchId, resultTeam1, resultTeam2);
            return ResponseEntity.ok(updatedPrediction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Obtener todas las predicciones de un usuario
    @GetMapping("/{userId}")
    public ResponseEntity<List<PredictionModel>> getPredictionsByUser(@PathVariable String userId) {
        List<PredictionModel> predictions = predictionService.getPredictionsByUser(userId);
        return ResponseEntity.ok(predictions);
    }
}
