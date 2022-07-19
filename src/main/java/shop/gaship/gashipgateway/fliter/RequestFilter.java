package shop.gaship.gashipgateway.fliter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import shop.gaship.gashipgateway.fliter.exception.CustomJsonProcessingException;
import shop.gaship.gashipgateway.fliter.exception.LogoutTokenRequestException;
import shop.gaship.gashipgateway.fliter.RequestFilter.Config;
import shop.gaship.gashipgateway.token.store.Payload;
import shop.gaship.gashipgateway.token.util.JwtTokenUtil;

/**
 * jwt 토큰 검증, 해당 정보 얻기 위한 기능이 존재하는 filter
 *
 * @author 조재철
 * @since 1.0
 */
@Slf4j
@Component
public class RequestFilter extends
    AbstractGatewayFilterFactory<Config> {

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

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {

            String accessToken = exchange.getRequest().getHeaders().get("X-AUTH-TOKEN").get(0);

            if (config.redisTemplate.opsForValue().get(accessToken) != null) {
                throw new LogoutTokenRequestException();
            }

            Payload payload = config.jwtTokenUtil.getPayloadFromToken(accessToken);

            ObjectMapper objectMapper = new ObjectMapper();
            String roles = null;
            try {
                roles = objectMapper.writeValueAsString(payload.getRole());
            } catch (JsonProcessingException e) {
                throw new CustomJsonProcessingException(e.getMessage());
            }

            exchange.getRequest()
                .mutate()
                .header("X-AUTH-ID", payload.getIdentificationNumber().toString())
                .header("X-AUTH-ROLE", roles)
                .build();

            return chain.filter(exchange);

        });
    }
}
