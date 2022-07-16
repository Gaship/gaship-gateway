package shop.gaship.gashipgateway.config;

import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import shop.gaship.gashipgateway.util.WebClientUtil;
import shop.gaship.gashipgateway.util.dto.SecureKeyResponse;


/**
 * packageName    : shop.gaship.gashipgateway.config fileName       : AuthenticationConfig author :
 * jo date           : 2022/07/15 description    : ===========================================================
 * DATE              AUTHOR             NOTE -----------------------------------------------------------
 * 2022/07/15        jo       최초 생성
 */

@Configuration
public class AuthenticationConfig {

    @Value("${secure.keymanager.url}")
    private String baseUrl;

    @Value("${secure.keymanager.appkey}")
    private String appKey;

    @Value("${secure.keymanager.jwt-secure-key}")
    private String jwtKeypair;

    @Bean
    public Key tokenKey() {
        SecureKeyResponse secureKey = new WebClientUtil<SecureKeyResponse>()
            .get(
                baseUrl,
                "/keymanager/v1.0/appkey/" + appKey + "/secrets/" + jwtKeypair,
                null,
                null,
                SecureKeyResponse.class
            ).getBody();

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        byte[] apiKeySecretBytes =
            DatatypeConverter.parseBase64Binary(secureKey.getBody().getSecret());

        return new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
    }
}