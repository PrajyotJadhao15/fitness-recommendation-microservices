package com.fitness.mailservice.DTO;

import lombok.Data;

@Data
public class AnalysisDTO {

    private String overall;
    private String pace;
    private String heartRate;
    private String caloriesBurned;
}
