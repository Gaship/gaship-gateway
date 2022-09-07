package shop.gaship.gashipgateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.gaship.gashipgateway.component.WhiteListChecker;
import shop.gaship.gashipgateway.fliter.WhiteListGlobalFilter;

/**
 * @author : 조재철
 * @since 1.0
 */
@Configuration
public class GlobalFilterConfiguration {
    @Bean
    public GlobalFilter whitelistGlobalFilter(WhiteListChecker whiteListChecker) {
        return new WhiteListGlobalFilter(whiteListChecker);
    }
}
