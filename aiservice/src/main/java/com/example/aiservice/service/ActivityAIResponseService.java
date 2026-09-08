package com.example.aiservice.service;

import com.example.aiservice.DTO.AIRecommendationResponseDTO;
import com.example.aiservice.DTO.RecommendationGeneratedEvent;
import com.example.aiservice.DTO.UserResponseDTO;
import com.example.aiservice.Exceptions.AiResponseParsingException;
import com.example.aiservice.Metrics.AiMetrics;
import com.example.aiservice.Repository.RecommendationRepository;
import com.example.aiservice.model.Activity;
import com.example.aiservice.model.Recommendation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityAIResponseService {

    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;
    private final PromptService promptService;
    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapperService recommendationMapperService;
    private final AiMetrics aiMetrics;
    private final RecommendationEventProducer recommendationEventProducer;
    private final UserClientService userClientService;
    private final RecommendationEventMapper recommendationEventMapper;

  //  private static final double COST_PER_TOKEN = 0.000001;



    public void generateRecommendation(Activity activity) {



          // Structured Prompt Creation.....
          String prompt = promptService.createPromptForAiService(activity);

          // Calling Gemini Service Which contains llm Call Logic...
          String response=geminiService.getRecommendations(prompt);

      log.info("AI Response: {}", response);



//            double totalTokens =
//                    usageMetadata.path("totalTokenCount").asDouble();
//
//       double alltokens=3274;
//
//            aiMetrics.getTotalTokensUsed()
//                    .increment(totalTokens);

//            log.info("Total Tokens Used: {}", totalTokens);
//
//            double estimatedCost = alltokens * COST_PER_TOKEN;
//
//            aiMetrics.getAiCost()
//                    .increment(estimatedCost);

         //   log.info("Estimated AI Cost: {}", estimatedCost);



        processAiResponse(activity, response);

    }


    private void processAiResponse(Activity activity, String response) {

        try {

            // GeminiService already returns only the generated content
            if (response == null || response.isBlank()) {
                throw new IllegalStateException(
                        "AI service returned an empty response"
                );
            }

            String cleanJson = response
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            // Extract JSON object in case Gemini adds surrounding text
            int start = cleanJson.indexOf("{");
            int end = cleanJson.lastIndexOf("}");

            if (start == -1 || end == -1 || start > end) {
                throw new IllegalStateException(
                        "No valid JSON object found in AI response: "
                                + cleanJson
                );
            }

            String cleanContent =
                    cleanJson.substring(start, end + 1);

            log.info(
                    "Response From Clean AI: {}",
                    cleanContent
            );

            // Convert AI JSON into DTO
            AIRecommendationResponseDTO aiRecommendationDTO =
                    objectMapper.readValue(
                            cleanContent,
                            AIRecommendationResponseDTO.class
                    );

            // Map DTO -> Recommendation entity
            Recommendation recommendation =
                    recommendationMapperService
                            .recommendationMapping(
                                    aiRecommendationDTO,
                                    activity
                            );

            // Save recommendation
            recommendationRepository.save(recommendation);

            // Get user ID
            Integer userId =
                    Integer.valueOf(activity.getUserId());

            // Get user details from User Service
            UserResponseDTO user =
                    userClientService.getUserById(userId);

            String email = user.getEmail();

            log.info(
                    "User email retrieved: {}",
                    email
            );

            // Create event
            RecommendationGeneratedEvent event =
                    recommendationEventMapper.map(
                            recommendation,
                            email
                    );

            // Publish event
            recommendationEventProducer.publish(event);

            // Metrics
            aiMetrics
                    .getRecommendationsGenerated()
                    .increment();

            log.info(
                    "Recommendation Response Saved: {}",
                    recommendation
            );

        } catch (Exception e) {

            log.error(
                    "AI Response not Saved: {}",
                    response,
                    e
            );

            throw new AiResponseParsingException(
                    "Failed to parse AI response",
                    e
            );
        }
    }

}
