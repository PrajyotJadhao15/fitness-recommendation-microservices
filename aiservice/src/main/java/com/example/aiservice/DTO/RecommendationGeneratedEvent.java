package com.example.aiservice.DTO;

import java.util.List;

public record RecommendationGeneratedEvent(
            String userId,
            String activityId,
            String email,
            AnalysisDTO analysis,
            List<ImprovementDTO> improvements,
            List<SuggestionDTO> suggestions,
            List<String> safety
    )
    {
    }

