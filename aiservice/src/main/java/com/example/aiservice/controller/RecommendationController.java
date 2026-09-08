package com.example.aiservice.controller;

import com.example.aiservice.DTO.RecommendationDTO;
import com.example.aiservice.Repository.RecommendationRepository;
import com.example.aiservice.model.Recommendation;
import com.example.aiservice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<RecommendationDTO>> getUserRecommendation(@PathVariable String userId){


        return ResponseEntity.ok(recommendationService.getUserRecommendation(userId));

    }

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<RecommendationDTO>  getActivityRecommendations(@PathVariable String activityId){

        return ResponseEntity.ok(recommendationService.getActivityRecommendation(activityId));
    }


    }


