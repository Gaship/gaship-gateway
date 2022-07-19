package shop.gaship.gashipgateway.config;

import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.time.Duration;
import java.util.Objects;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import shop.gaship.gashipgateway.config.dto.SecureKeyResponse;
import shop.gaship.gashipgateway.config.exceptions.NoResponseDataException;


/**
 * secure key와 관련된 설정을 위한 class 입니다.
 *
 * @author 조재철
 * @since 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "secure-key-manager")
public class AuthenticationConfig {

    private String url;
    private String appkey;
    private String jwtSecureKey;

    @Bean
    public Key tokenKey() {
        String secretJwtKey = findSecretDataFromSecureKeyManager(jwtSecureKey);
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes =
            DatatypeConverter.parseBase64Binary(secretJwtKey);

        return new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
    }

    String findSecretDataFromSecureKeyManager(String keyId) {
        String errorMessage = "응답 결과가 없습니다.";
        return Objects.requireNonNull(WebClient.create(url).get()
                .uri("/keymanager/v1.0/appkey/{appkey}/secrets/{keyid}", appkey, keyId)
                .retrieve()
                .toEntity(SecureKeyResponse.class)
                .timeout(Duration.ofSeconds(5))
                .blockOptional()
                .orElseThrow(() -> new NoResponseDataException(errorMessage))
                .getBody())
            .getBody()
            .getSecret();
    }

    public String getUrl() {
        return url;
    }

    public String getAppkey() {
        return appkey;
    }

    public String getJwtSecureKey() {
        return jwtSecureKey;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public void setJwtSecureKey(String jwtSecureKey) {
        this.jwtSecureKey = jwtSecureKey;
    }
}