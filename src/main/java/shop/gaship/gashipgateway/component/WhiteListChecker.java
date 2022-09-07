package shop.gaship.gashipgateway.component;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 요청한 ip 가 지정한 white list 에 존재하는지 판단하는 클래스 입니다.
 *
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

    /**
     * white list 에 존재하는지 하지 않는지 판단하여 존재하지 않을 시 통과를 막기 위한 메서드 입니다.
     *
     * @param remoteAddress 요청을 한 곳의 address 입니다.
     * @return 요청한 address 가 white list 에 포함되면 false, 포함되지 않으면 true를 반환합니다.
     */
    public boolean blocked(String remoteAddress) {
        StringTokenizer stringTokenizer = new StringTokenizer(whitelist, ",");

        List<String> remoteAddressWhiteList = new ArrayList<>();

        while (stringTokenizer.hasMoreTokens()) {
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
