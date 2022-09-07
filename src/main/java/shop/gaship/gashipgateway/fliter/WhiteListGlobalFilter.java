package shop.gaship.gashipgateway.fliter;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ipresolver.RemoteAddressResolver;
import org.springframework.cloud.gateway.support.ipresolver.XForwardedRemoteAddressResolver;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import shop.gaship.gashipgateway.component.WhiteListChecker;

/**
 * 요청한 ip 가 white list 에 존재하는지 판단하는 글로벌 필터 입니다.
 *
 * @author : 조재철
 * @since 1.0
 */
@RequiredArgsConstructor
@Component
public class WhiteListGlobalFilter implements GlobalFilter, Ordered {

    private final WhiteListChecker whiteListChecker;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String remoteAddress = getRemoteAddress(exchange);

        if (whiteListChecker.blocked(remoteAddress)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }

        return chain.filter(exchange);
    }

    private String getRemoteAddress(ServerWebExchange exchange) {
        RemoteAddressResolver resolver = XForwardedRemoteAddressResolver.maxTrustedIndex(1);
        return resolver.resolve(exchange).getAddress().getHostAddress();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
