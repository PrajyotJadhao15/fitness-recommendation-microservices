package com.example.aiservice.service;

import com.example.aiservice.DTO.AnalysisDTO;
import com.example.aiservice.DTO.ImprovementDTO;
import com.example.aiservice.DTO.RecommendationGeneratedEvent;
import com.example.aiservice.DTO.SuggestionDTO;
import com.example.aiservice.model.Analysis;
import com.example.aiservice.model.Recommendation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationEventMapper {





        public RecommendationGeneratedEvent map(
                Recommendation recommendation,
                String email) {

            Analysis analysis = recommendation.getAnalysis();

            AnalysisDTO analysisDTO = new AnalysisDTO();
            analysisDTO.setOverall(analysis.getOverall());
            analysisDTO.setPace(analysis.getPace());
            analysisDTO.setHeartRate(analysis.getHeartRate());
            analysisDTO.setCaloriesBurned(analysis.getCaloriesBurned());

            List<ImprovementDTO> improvements = recommendation.getImprovements()
                    .stream()
                    .map(item -> {
                        ImprovementDTO dto = new ImprovementDTO();
                        dto.setArea(item.getArea());
                        dto.setRecommendation(item.getRecommendation());
                        return dto;
                    })
                    .toList();

            List<SuggestionDTO> suggestions = recommendation.getSuggestions()
                    .stream()
                    .map(item -> {
                        SuggestionDTO dto = new SuggestionDTO();
                        dto.setWorkout(item.getWorkout());
                        dto.setDescription(item.getDescription());
                        return dto;
                    })
                    .toList();

            return new RecommendationGeneratedEvent(
                    recommendation.getUserId(),
                    recommendation.getActivityId(),
                    email,
                    analysisDTO,
                    improvements,
                    suggestions,
                    recommendation.getSafety()
            );
        }
    }
