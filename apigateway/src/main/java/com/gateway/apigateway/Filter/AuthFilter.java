package com.gateway.apigateway.Filter;

import com.gateway.apigateway.config.JwtUtility;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter implements GlobalFilter {

    private final JwtUtility jwtUtility;

    public AuthFilter(JwtUtility jwtUtility) {
        this.jwtUtility = jwtUtility;
        System.out.println("AuthFilter bean created");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        // public APIs
        String path = exchange.getRequest().getPath().toString();


        if (path.contains("/api/users/login") || path.contains("/api/users/register")) {
            return chain.filter(exchange);
        }

//        if (path.contains("/api/users/login") || path.contains("/api/users/register")) {
//            return chain.filter(exchange);
//        }
System.out.println("Path: "+path);
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (!jwtUtility.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String email = jwtUtility.extractEmail(token);

        exchange = exchange.mutate()
                .request(request -> request.header("X-User-Email", email))
                .build();


        return chain.filter(exchange);



    }


}



