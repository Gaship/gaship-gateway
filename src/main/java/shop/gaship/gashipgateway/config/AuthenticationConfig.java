package shop.gaship.gashipgateway.config;

import io.jsonwebtoken.SignatureAlgorithm;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Objects;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import shop.gaship.gashipgateway.config.dto.response.SecureKeyResponseDto;
import shop.gaship.gashipgateway.config.exceptions.NoResponseDataException;

/**
 * secure key와 관련된 설정을 위한 class.
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

    private String localKey;

    /**
     * secure key를 얻어온 후 Base64로 인코딩 한 것을 parsing 한 것, HS256 알고리즘을 가지는 SecretKeySpec을 만드는 메서드.
     *
     * @return SecretKeySpec 객체를 반환.
     */
    @Bean
    public Key tokenKey() {
        String secretJwtKey;

        try {
            secretJwtKey = findSecretDataFromSecureKeyManager(jwtSecureKey);
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException
                | UnrecoverableKeyException | IOException | KeyManagementException e) {
            throw new NoResponseDataException("NHN Secucre Excpetion");
        }

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes =
                DatatypeConverter.parseBase64Binary(secretJwtKey);

        return new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
    }

    /**
     * nhn cloud key manager에 secure key를 얻기 위한 메서드.
     *
     * @param keyId secure key를 얻기 위해 필요한 key id.
     * @return 원하는 secure key를 반환.
     * @throws CertificateException 인증서의 encode 문제, 유효하지 않은 경우 예외 발생.
     * @throws NoSuchAlgorithmException 암호 알고리즘이 요구되었음에도 불구하고, 현재의 환경에서는 사용 가능하지 않은 경우에 예외 발생.
     * @throws KeyStoreException 키스토어 예외 발생.
     * @throws UnrecoverableKeyException key를 복원할 수 없는 경우에 예외를 발생.
     * @throws IOException I/O 오류가 발생하는 경우에 throw되는 예외 발생.
     * @throws KeyManagementException 키 관리를 다루는 모든 작업에 대한 일반적인 키 관리 예외 발생.
     */
    String findSecretDataFromSecureKeyManager(String keyId)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException,
            UnrecoverableKeyException, IOException, KeyManagementException {
        KeyStore clientStore = KeyStore.getInstance("PKCS12");
        clientStore.load(new FileInputStream(ResourceUtils.getFile("classpath:github-action.p12")),
                localKey.toCharArray());

        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
        sslContextBuilder.setProtocol("TLS");
        sslContextBuilder.loadKeyMaterial(clientStore, localKey.toCharArray());
        sslContextBuilder.loadTrustMaterial(new TrustSelfSignedStrategy());

        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                sslContextBuilder.build());
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build();
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        return Objects.requireNonNull(new RestTemplate(requestFactory)
                        .getForEntity(url + "/keymanager/v1.0/appkey/{appkey}/secrets/{keyid}",
                                SecureKeyResponseDto.class,
                                this.appkey,
                                keyId)
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

    public void setLocalKey(String localKey) {
        this.localKey = localKey;
    }
}
