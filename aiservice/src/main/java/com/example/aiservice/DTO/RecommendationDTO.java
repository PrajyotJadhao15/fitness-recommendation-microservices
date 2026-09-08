package com.example.aiservice.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDTO {


    private String id;

    private String activityId;

    private String userId;

    private String recommendation;

    private AnalysisDTO analysis;

    private List<ImprovementDTO> improvements;

    private  List<SuggestionDTO> suggestions;

    private List<String> safety;

    @JsonIgnore
    private LocalDateTime createdAt;


}
