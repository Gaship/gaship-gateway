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
    private String coupons;

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

    public String getCoupons() {
        return coupons;
    }

    public void setCoupons(String coupons) {
        this.coupons = coupons;
    }

    /**
     * gateway로 들어온 요청을 라우팅 해주거나, 필터를 통해 필터링을 하기 위한 메서드.
     *
     * @param routeLocatorBuilder predicates, fileters들을 routes에 추가해 요청/응답을 라우팅.
     * @param requestFilter       요청에 대해 필터링해주는 객체.
     * @param redisTemplate       Redis 데이터에 쉽게 접근하기 위한 코드를 제공해주는 객체.
     * @param jwtTokenUtil        token 검증을 위한 유틸 객체.
     * @return routeLocatorBuilder가 라우팅 한 결과를 반환.
     */
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder,
        RequestFilter requestFilter, RedisTemplate redisTemplate, JwtTokenUtil jwtTokenUtil) {
        return routeLocatorBuilder.routes()
                .route(p -> p.path("/securities/issue-token")
                        .uri(auth))
                .route(p -> p.path("/securities/verify/email/**")
                        .uri(auth))
                .route(p -> p.path("/securities/logout")
                    .uri(auth))
                .route(p -> p.path("/securities/**")
                        .filters(f -> f.filter(
                                requestFilter.apply(
                                        new RequestFilter.Config(redisTemplate, jwtTokenUtil))))
                        .uri(auth))
                .route(p -> p.path("/payments/**")
                        .uri(payments))
                .route(p -> p.path("/schedulers/**")
                        .uri(schedulers))
                .route(p -> p.path("/api/coupons/**")
                        .uri(coupons))
                .route(p -> p.path("/**")
                        .uri(shoppingmall))
                .build();
    }

    /**
     * CustomWebExceptionHandler를 반환할 빈을 등록.
     *
     * @return 만들어둔 CustomWebExceptionHandler 객체를 반환.
     */
    @Bean
    public ErrorWebExceptionHandler customWebExceptionHandler() {
        return new CustomWebExceptionHandler();
    }

    /**
     * 필터를 거치면서 발생한 에러를 핸들링하기 위한 클래스.
     *
     * @author 조재철
     * @since 1.0
     */
    public static class CustomWebExceptionHandler implements ErrorWebExceptionHandler {

        /**
         * 라우팅 혹은 필터링 중 에러가 발생한 경우 핸들링 하기 위한 메서드.
         *
         * @param exchange http 요청과 응답에 대한 컨텍스트.
         * @param ex       예외 발생시 넘어올 예외 클래스.
         * @return 에러로 종료시키는 Mono 컨테이너를 생성하는 정적 메소드 반환.
         */
        @Override
        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

            return Mono.error(ex);
        }
    }
}
