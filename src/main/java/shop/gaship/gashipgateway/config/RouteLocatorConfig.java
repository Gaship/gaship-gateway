package shop.gaship.gashipgateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import shop.gaship.gashipgateway.fliter.RequestFilter;
import shop.gaship.gashipgateway.token.util.JwtTokenUtil;

/**
 * 라우팅 설정을 위한 class.
 *
 * @author 조재철
 * @since 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "gaship-server-url")
public class RouteLocatorConfig {

    private String auth;
    private String shoppingmall;
    private String payments;
    private String schedulers;

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getShoppingmall() {
        return shoppingmall;
    }

    public void setShoppingmall(String shoppingmall) {
        this.shoppingmall = shoppingmall;
    }

    public String getPayments() {
        return payments;
    }

    public void setPayments(String payments) {
        this.payments = payments;
    }

    public String getSchedulers() {
        return schedulers;
    }

    public void setSchedulers(String schedulers) {
        this.schedulers = schedulers;
    }

    /**
     * gateway로 들어온 요청을 라우팅 해주거나, 필터를 통해 필터링을 하기 위한 메서드.
     *
     * @param routeLocatorBuilder
     * @param requestFilter
     * @param redisTemplate
     * @param jwtTokenUtil
     * @return
     */
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder,
        RequestFilter requestFilter, RedisTemplate redisTemplate, JwtTokenUtil jwtTokenUtil) {
        return routeLocatorBuilder.routes()
            .route(p -> p.path("/securities/issue-token")
                .uri(auth))
            .route(p -> p.path("/securities/logout")
                .uri(auth))
            .route(p -> p.path("/securities/**")
                .filters(f -> f.filter(
                    requestFilter.apply(new RequestFilter.Config(redisTemplate, jwtTokenUtil))))
                .uri(auth))
            .route(p -> p.path("/payments/**")
                .uri(payments))
            .route(p -> p.path("/schedulers/**")
                .uri(schedulers))
            .route(p -> p.path("/**")
                .uri(shoppingmall))
            .build();
    }

    /**
     * CustomWebExceptionHandler를 반환할 빈을 등록.
     *
     * @return
     */
    @Bean
    public ErrorWebExceptionHandler myExceptionHandler() {
        return new CustomWebExceptionHandler();
    }

    public static class CustomWebExceptionHandler implements ErrorWebExceptionHandler {

        /**
         * 라우팅 혹은 필터링 중 에러가 발생한 경우 핸들링 하기 위한 메서드.
         *
         * @param exchange
         * @param ex
         * @return
         */
        @Override
        public Mono<Void> handle(
            ServerWebExchange exchange, Throwable ex) {

            return Mono.error(ex);
        }
    }
}
