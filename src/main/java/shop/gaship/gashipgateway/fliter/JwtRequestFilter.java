package shop.gaship.gashipgateway.fliter;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import shop.gaship.gashipgateway.fliter.JwtRequestFilter.Config;
import shop.gaship.gashipgateway.token.util.JwtTokenUtil;

/**
 * packageName    : shop.gaship.gashipgateway.fliter fileName       : JwtRequestFilter author
 *  : jo date           : 2022/07/15 description    : ===========================================================
 * DATE              AUTHOR             NOTE -----------------------------------------------------------
 * 2022/07/15        jo       최초 생성
 */
@Component
public class JwtRequestFilter extends
    AbstractGatewayFilterFactory<Config> {

    public JwtRequestFilter(JwtTokenUtil jwtTokenUtil) {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();


            // cookie에 token 이 존재하지 않을 때
            if (!request.getHeaders().containsKey("X-AUTH-TOKEN")) {
                return chain.filter(exchange);
//                return handleUnAuthorized(exchange); // 401 Error
            }

            // cookie 에서 token 문자열 받아오기
            String accessToken = request.getHeaders().get("X-AUTH-TOKEN").get(0);


            if (!config.jwtTokenUtil.validateToken(accessToken)) {
//                return handleUnAuthorized(exchange); // 토큰이 일치하지 않을 때
                WebClient webClient = WebClient.builder()
                    .baseUrl("http://localhost:9090")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();

                webClient.get()
                    .uri("/refreshToken")
                    .header("host", request.getURI().getHost())
                    .header("path", request.getURI().getPath())
                    .header("method", request.getMethodValue());
            }

            return chain.filter(exchange); // 토큰이 일치할 때

        });
    }

    private Mono<Void> handleUnAuthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    public static class Config {
        private final JwtTokenUtil jwtTokenUtil;

        public Config(JwtTokenUtil jwtTokenUtil) {
            this.jwtTokenUtil = jwtTokenUtil;
        }
    }
}
