package com.fitness.mailservice.service;

import com.fitness.mailservice.DTO.RecommendationGeneratedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationConsumerService {

    private final EmailService emailService;

    @KafkaListener(
            topics = "${kafka.topic.recommendation}",
            groupId = "mail_service_group"
    )
    public void consumeRecommendation(
            RecommendationGeneratedEvent event) {

        log.info(
                "Received recommendation event for userId={}, email={}",
                event.getUserId(),
                event.getEmail()
        );

        emailService.sendRecommendationEmail(event);
    }
}