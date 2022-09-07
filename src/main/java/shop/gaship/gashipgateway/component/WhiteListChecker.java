package shop.gaship.gashipgateway.component;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author : 조재철
 * @since 1.0
 */
@Slf4j
@ConfigurationProperties(prefix = "remote-address")
@Component
public class WhiteListChecker {

    private String whitelist;

    public void setWhitelist(String whitelist) {
        this.whitelist = whitelist;
    }

    public boolean blocked(String remoteAddress) {
        StringTokenizer stringTokenizer = new StringTokenizer(whitelist, ",");

        List<String> remoteAddressWhiteList = new ArrayList<>();

        while (stringTokenizer.hasMoreTokens()) {
            log.warn("remote address : {}", remoteAddress);

            remoteAddressWhiteList.add(stringTokenizer.nextToken());
        }


        for (String whiteRemoteAddress : remoteAddressWhiteList) {
            if (remoteAddress.equals(whiteRemoteAddress)) {
                return false;
            }
        }

        return true;
    }
}
