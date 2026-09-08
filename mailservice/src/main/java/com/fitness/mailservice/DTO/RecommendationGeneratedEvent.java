package com.fitness.mailservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationGeneratedEvent {

    private String userId;
    private String email;

    private String activityId;

    private String recommendation;

    private AnalysisDTO analysis;

    private List<ImprovementDTO> improvements;

    private List<SuggestionDTO> suggestions;

    private List<String> safety;
}
