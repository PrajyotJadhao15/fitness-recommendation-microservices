package com.example.aiservice.service;

import com.example.aiservice.DTO.RecommendationGeneratedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationEventProducer {


        private final KafkaTemplate<String, RecommendationGeneratedEvent> kafkaTemplate;

        private static final String TOPIC = "recommendation.generated";

        public void publish(RecommendationGeneratedEvent event) {

            kafkaTemplate.send(
                    TOPIC,
                    event.userId(),
                    event
            );

            log.info(
                    "Recommendation event published for userId={}",
                    event.userId()
            );
        }
    }

