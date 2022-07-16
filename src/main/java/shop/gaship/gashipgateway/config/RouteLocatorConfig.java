package shop.gaship.gashipgateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.gaship.gashipgateway.fliter.JwtRequestFilter;
import shop.gaship.gashipgateway.token.util.JwtTokenUtil;

@Configuration
public class RouteLocatorConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder,
        JwtRequestFilter jwtRequestFilter, JwtTokenUtil jwtTokenUtil) {
        return routeLocatorBuilder.routes()
            .route(p -> p.path("/securities/**")
                .filters(f -> f.filter(
                    jwtRequestFilter.apply(new JwtRequestFilter.Config(jwtTokenUtil))))
                .uri("http://localhost:7071"))
            .route(p -> p.path("/payments/**")
                .uri("http://172.30.1.23:7073"))
            .route(p -> p.path("/schedulers/**")
                .uri("http://172.30.1.23:7074"))
            .route(p -> p.path("/**")
                .uri("http://172.30.1.23:7072"))
            .build();
    }
}
