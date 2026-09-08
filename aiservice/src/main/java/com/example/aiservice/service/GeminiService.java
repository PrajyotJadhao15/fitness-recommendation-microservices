package com.example.aiservice.service;


import com.example.aiservice.Exceptions.GeminiApiException;
import com.example.aiservice.Metrics.AiMetrics;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;

import org.springframework.stereotype.Service;


import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    private final AiMetrics aiMetrics;
    private final MeterRegistry meterRegistry;
    private final ChatClient chatClient;


    @CircuitBreaker(
            name = "geminiApi",
            fallbackMethod = "fallbackRecommendation"
    )
        public String getRecommendations(String prompt){

        aiMetrics.getAiCalls().increment();

        Timer.Sample sample = Timer.start(meterRegistry);

            try {


                //WebClient(Asynchronous Communication)...
/*
                String response = webClient.post()
                        .uri(Gemini_Url)
                        .header("Content-Type", "application/json")
                        .header("x-goog-api-key", Gemini_Api_Key)
                        .bodyValue(requestBody)
                        .retrieve()
                        .onStatus(
                                HttpStatusCode::isError,
                                clientResponse -> clientResponse.bodyToMono(String.class)
                                        .flatMap(errorBody -> {
                                            log.error("Gemini Error Response: {}", errorBody);
                                            return reactor.core.publisher.Mono.error(
                                                    new RuntimeException(errorBody));
                                        })
                        )
                        .bodyToMono(String.class)
                        .block();
*/

                return chatClient
                        .prompt()
                        .user(prompt)
                        .call()
                        .content();

               // WebClientResponseException

            } catch (GeminiApiException e) {

                log.error("Error while calling Gemini API", e);

                aiMetrics.getAiFailures().increment();
                throw e;
            }
            catch (Exception ex){

                aiMetrics.getAiFailures().increment();

                log.error("Unexpected error while calling Gemini API", ex);

                throw new GeminiApiException(
                        "Gemini API unavailable",
                        ex
                );

            } finally {

                sample.stop(aiMetrics.getAiResponseTimer());
            }
        }

    public String fallbackRecommendation(String prompt, Exception ex) {

        log.error("Gemini API unavailable: {}", ex.getMessage());

        throw new GeminiApiException(
                "Gemini service temporarily unavailable",
                ex
        );



    }



}
