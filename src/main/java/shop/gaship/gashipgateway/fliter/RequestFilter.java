package shop.gaship.gashipgateway.fliter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import shop.gaship.gashipgateway.fliter.RequestFilter.Config;
import shop.gaship.gashipgateway.fliter.exception.CustomJsonProcessingException;
import shop.gaship.gashipgateway.fliter.exception.LogoutTokenRequestException;
import shop.gaship.gashipgateway.token.store.Payload;
import shop.gaship.gashipgateway.token.util.JwtTokenUtil;

/**
 * jwt 토큰 검증, 해당 정보 얻기 위한 기능이 존재하는 filter.
 *
 * @author 조재철
 * @since 1.0
 */
@Slf4j
@Component
public class RequestFilter extends
    AbstractGatewayFilterFactory<Config> {

    /**
     * Filter에서 사용되는 필요한 설정을 담은 클래스.
     *
     * @author 조재철
     * @since 1.0
     */
    public static class Config {

        private final RedisTemplate redisTemplate;
        private final JwtTokenUtil jwtTokenUtil;

        public Config(RedisTemplate redisTemplate, JwtTokenUtil jwtTokenUtil) {
            this.redisTemplate = redisTemplate;
            this.jwtTokenUtil = jwtTokenUtil;
        }
    }


    public RequestFilter(RedisTemplate redisTemplate, JwtTokenUtil jwtTokenUtil) {
        super(Config.class);
    }

    /**
     * filter 에서 jwt 의 유효기간을 검증 하거나 유효한 토큰인 경우 토큰에 담긴 정보를 헤더에 담는 필터링을 해주기 위한 메서드.
     *
     * @param config Filter 에서 사용되는 필요한 설정을 담은 객체
     * @return 요청에 대해 필터링을 하여 조건에 따라 분기결과 반환.
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            if (exchange.getRequest().getHeaders().get("X-AUTH-TOKEN") == null) {
                return chain.filter(exchange);
            }

            String accessToken =
                exchange.getRequest().getHeaders().get("X-AUTH-TOKEN").get(0);

            if (config.redisTemplate.opsForValue().get(accessToken) != null) {
                throw new LogoutTokenRequestException();
            }

            Payload payload = config.jwtTokenUtil.getPayloadFromToken(accessToken);

            exchange.getRequest()
                    .mutate()
                    .header("X-AUTH-ID", payload.getIdentificationNumber().toString())
                    .header("X-AUTH-ROLE", payload.getRole())
                    .build();

            return chain.filter(exchange);

        };
    }
}
