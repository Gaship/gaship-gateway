package shop.gaship.gashipgateway.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import shop.gaship.gashipgateway.fliter.exception.CustomJsonProcessingException;
import shop.gaship.gashipgateway.fliter.exception.LogoutTokenRequestException;
import shop.gaship.gashipgateway.fliter.RequestFilter;
import shop.gaship.gashipgateway.token.util.JwtTokenUtil;

/**
 * 라우팅 설정을 위한 class 입니다.
 *
 * @author 조재철
 * @since 1.0
 */
@Configuration
public class RouteLocatorConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder,
        RequestFilter requestFilter, RedisTemplate redisTemplate, JwtTokenUtil jwtTokenUtil) {
        return routeLocatorBuilder.routes()
            .route(p -> p.path("/securities/reissueJwt")
                .uri("http://localhost:7071"))
            .route(p -> p.path("/securities/**")
                .filters(f -> f.filter(
                    requestFilter.apply(new RequestFilter.Config(redisTemplate, jwtTokenUtil))))
                .uri("http://localhost:7071"))
            .route(p -> p.path("/payments/**")
                .uri("http://localhost:7073"))
            .route(p -> p.path("/schedulers/**")
                .uri("http://localhost:7074"))
            .route(p -> p.path("/**")
                .uri("http://localhost:7072"))
            .build();
    }

    @Bean
    public ErrorWebExceptionHandler myExceptionHandler() {
        return new CustomWebExceptionHandler();
    }

    public static class CustomWebExceptionHandler implements ErrorWebExceptionHandler {

        private String errorCodeMaker(int errorCode) {
            return "{\"errorCode\":" + errorCode + "}";
        }

        @Override
        public Mono<Void> handle(
            ServerWebExchange exchange, Throwable ex) {
            int errorCode = 999;
            if (ex.getClass() == NullPointerException.class) {
                errorCode = 61;
            } else if (ex.getClass() == ExpiredJwtException.class) {
                errorCode = 56;
            } else if (ex.getClass() == MalformedJwtException.class
                || ex.getClass() == SignatureException.class
                || ex.getClass() == UnsupportedJwtException.class) {
                errorCode = 55;
            } else if (ex.getClass() == IllegalArgumentException.class) {
                errorCode = 51;
            } else if (ex.getClass() == CustomJsonProcessingException.class) {
                errorCode = 66;
            } else if (ex.getClass() == LogoutTokenRequestException.class) {
                errorCode = 99;
            }

            byte[] bytes = errorCodeMaker(errorCode).getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

            return exchange.getResponse().writeWith(Flux.just(buffer));
        }
    }
}
