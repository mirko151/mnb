package com.soa.trello.gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
public class GatewayConfig {
  @Value("${JWT_SECRET}") private String jwtSecret;
  @Value("${USERS_URL}") private String usersUrl;
  @Value("${PROJECTS_URL}") private String projectsUrl;
  @Value("${TASKS_URL}") private String tasksUrl;
  @Value("${NOTIFS_URL}") private String notifsUrl;
  @Value("${WORKFLOW_URL}") private String workflowUrl;
  @Value("${ANALYTICS_URL}") private String analyticsUrl;

  private SecretKey key() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }

  @Bean
  public RouteLocator routes(RouteLocatorBuilder rlb){
    return rlb.routes()
      .route("users-open", r -> r.path("/users/auth/**").uri(usersUrl))
      .route("users", r -> r.path("/users/**").filters(f -> f.filter(auth())).uri(usersUrl))
      .route("projects", r -> r.path("/projects/**").filters(f -> f.filter(auth())).uri(projectsUrl))
      .route("tasks", r -> r.path("/tasks/**").filters(f -> f.filter(auth())).uri(tasksUrl))
      .route("notifications", r -> r.path("/notifications/**").filters(f -> f.filter(auth())).uri(notifsUrl))
      .route("workflow", r -> r.path("/workflow/**").filters(f -> f.filter(auth())).uri(workflowUrl))
      .route("analytics", r -> r.path("/analytics/**").filters(f -> f.filter(auth())).uri(analyticsUrl))
      .build();
  }

  private GatewayFilter auth() {
    return (exchange, chain) -> {
      String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
      if (auth == null || !auth.startsWith("Bearer ")) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
      }
      try {
        Claims c = Jwts.parserBuilder().setSigningKey(key()).build()
            .parseClaimsJws(auth.substring(7)).getBody();

        ServerHttpRequest mutatedReq = exchange.getRequest().mutate()
          .header("X-User-Id", String.valueOf(c.get("userId")))
          .header("X-User-Role", String.valueOf(c.get("role")))
          .build();

        ServerWebExchange mutatedEx = exchange.mutate().request(mutatedReq).build();
        return chain.filter(mutatedEx);
      } catch (Exception e) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
      }
    };
  }
}
