package com.example.aiservice.service;

import com.example.aiservice.DTO.AIRecommendationResponseDTO;
import com.example.aiservice.Repository.RecommendationRepository;
import com.example.aiservice.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@Slf4j
@RequiredArgsConstructor
class RecommendationMapperService {

    private final ModelMapper modelMapper;
    private final RecommendationRepository recommendationRepository;


    public Recommendation recommendationMapping(AIRecommendationResponseDTO DTO, Activity activity){




        log.info("Improvements DTO = {}", DTO.getImprovements());
        log.info("Suggestions DTO = {}", DTO.getSuggestions());


        log.info("Improvements  = {}", DTO.getImprovements().stream()
                .map(dto -> modelMapper.map(dto, Improvements.class))
                .toList());
        log.info("Suggestions  = {}", DTO.getSuggestions().stream()
                .map(dto -> modelMapper.map(dto, Suggestion.class)).toList());


        return Recommendation.builder()
                 .activityId(activity.getId())
                 .userId(activity.getUserId())
                 .analysis(modelMapper.map(
                         DTO.getAnalysis(), Analysis.class)
                 )
                 .improvements(DTO.getImprovements()
                         .stream()
                         .map(dto -> modelMapper.map(dto, Improvements.class))
                         .toList())


                 .suggestions(DTO.getSuggestions().stream()
                         .map(dto -> modelMapper.map(dto, Suggestion.class)).toList())
                 .safety(DTO.getSafety())
                 .createdAt((LocalDateTime.now()))
                 .build();

    }

}
