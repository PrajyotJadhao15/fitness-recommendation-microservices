package com.example.aiservice.service;

import com.example.aiservice.DTO.UserResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class UserClientService {

    private final WebClient webClient;

    public UserClientService(WebClient webClient) {
        this.webClient = webClient;
    }


    public UserResponseDTO getUserById(Integer userId){
        return webClient
                .get()
                .uri("http://USER-SERVICE/api/users/fetch/{userId}", userId)
                .retrieve()
                .bodyToMono(UserResponseDTO.class)
                .block();
    }
}
