package shop.gaship.gashipgateway.util;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import shop.gaship.gashipgateway.util.exceptions.NoResponseDataException;

/**
 * packageName    : shop.gaship.gashipauth.util
 * fileName       : WebClientUtil
 * author         : 김민수
 * date           : 2022/07/09
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/07/09        김민수           최초 생성
 * 2022/07/10        김민수           웹클라이언트 결과 타입변경
 */
@Slf4j
public class WebClientUtil<T> {
    private static final String ERROR_MESSAGE = "응답결과가 존재하지 않습니다.";
    private static final Duration timeOut = Duration.of(5, ChronoUnit.SECONDS);

    /**
     * methodName : get
     * author : 김민수
     * description : REST API get 메서드를 요청해주는 유틸성 메서드입니다.
     *
     * @param baseUrl : 기본 url
     * @param urn : 서버에 요청 할 urn입니다.
     * @param queryParams : 쿼리 파라미터들입니다.
     * @param headers : http 헤더들입니다.
     * @param responseEntity : 요청받는 객체 타입입니다.
     * @return ResponseEntity<T> : 서버의 응답 값이 담긴 객체입니다.
     */
    public ResponseEntity<T> get(String baseUrl, String urn,
                 @Nullable List<QueryParam> queryParams,
                 @Nullable Map<String, List<String>> headers,
                 Class<T> responseEntity) {
        return createMethod(() -> WebClient.create(baseUrl).get()
            , urn, queryParams, headers, responseEntity);
    }

    /**
     * methodName : post
     * author : 김민수
     * description : REST API post 메서드를 요청해주는 유틸성 메서드입니다.
     *
     * description : REST API post 메서드를 요청해주는 유틸성 메서드입니다.
     *
     * @param baseUrl : 기본 url
     * @param urn : 서버에 요청 할 urn입니다.
     * @param queryParams : 쿼리 파라미터들입니다.
     * @param headers : http 헤더들입니다.
     * @param bodyValue : 서버에게 필요한 body data를 제공하기 위해 사용합니다..
     * @param responseEntity : 요청받는 객체 타입입니다.
     * @return ResponseEntity<T> : 서버의 응답 값이 담긴 객체입니다.
     */
    public <U> ResponseEntity<T> post(String baseUrl, String urn,
                      @Nullable List<QueryParam> queryParams,
                      @Nullable Map<String, List<String>> headers,
                      U bodyValue,
                      Class<T> responseEntity) {
        return createHasBodyMethod(() -> WebClient.create(baseUrl).post()
            , urn, queryParams, headers, bodyValue, responseEntity);
    }

    /**
     * methodName : put
     * author : 김민수
     * description : REST API put 메서드를 요청해주는 유틸성 메서드입니다.
     *
     * @param baseUrl : 기본 url
     * @param urn : 서버에 요청 할 urn입니다.
     * @param queryParams : 쿼리 파라미터들입니다.
     * @param headers : http 헤더들입니다.
     * @param bodyValue : 서버에게 필요한 body data를 제공하기 위해 사용합니다..
     * @param responseEntity : 요청받는 객체 타입입니다.
     * @return ResponseEntity<T> : 서버의 응답 값이 담긴 객체입니다.
     */
    public <U> ResponseEntity<T> put(String baseUrl, String urn,
                     @Nullable List<QueryParam> queryParams,
                     @Nullable Map<String, List<String>> headers,
                     U bodyValue,
                     Class<T> responseEntity) {
        return createHasBodyMethod(() -> WebClient.create(baseUrl).put()
            , urn, queryParams, headers, bodyValue, responseEntity);
    }


    /**
     * methodName : patch
     * author : 김민수
     * description : REST API patch 메서드를 요청해주는 유틸성 메서드입니다.
     *
     * @param baseUrl : 기본 url
     * @param urn : 서버에 요청 할 urn입니다.
     * @param queryParams : 쿼리 파라미터들입니다.
     * @param headers : http 헤더들입니다.
     * @param bodyValue : 서버에게 필요한 body data를 제공하기 위해 사용합니다..
     * @param responseEntity : 요청받는 객체 타입입니다.
     * @return ResponseEntity<T> : 서버의 응답 값이 담긴 객체입니다.
     */
    public <U> ResponseEntity<T> patch(String baseUrl, String urn,
                       @Nullable List<QueryParam> queryParams,
                       @Nullable Map<String, List<String>> headers,
                       U bodyValue,
                       Class<T> responseEntity) {
        return createHasBodyMethod(() -> WebClient.create(baseUrl).patch()
            , urn, queryParams, headers, bodyValue, responseEntity);
    }

    /**
     * methodName : delete
     * author : 김민수
     * description : REST API delete 메서드를 요청해주는 유틸성 메서드입니다.
     *
     * @param baseUrl : 기본 url
     * @param urn : 서버에 요청 할 urn입니다.
     * @param queryParams : 쿼리 파라미터들입니다.
     * @param headers : http 헤더들입니다.
     * @param responseEntity : 요청받는 객체 타입입니다.
     * @return ResponseEntity<T> : 서버의 응답 값이 담긴 객체입니다.
     */
    public ResponseEntity<T> delete(String baseUrl, String urn,
                    @Nullable List<QueryParam> queryParams,
                    @Nullable Map<String, List<String>> headers,
                    Class<T> responseEntity) {
        return createMethod(() -> WebClient.create(baseUrl).delete()
            , urn, queryParams, headers, responseEntity);
    }

    private ResponseEntity<T> createMethod(Supplier<WebClient.RequestHeadersUriSpec<?>> restMethod, String urn,
                               List<QueryParam> queryParams,
                               Map<String, List<String>> headers, Class<T> responseEntity) {
        return restMethod.get()
            .uri(uriBuilder -> setQueryParams(uriBuilder.path(urn), queryParams).build())
            .headers(httpHeaders -> setHeaders(httpHeaders, headers))
            .retrieve()
            .toEntity(responseEntity)
            .timeout(timeOut)
            .blockOptional()
            .orElseThrow(() -> new NoResponseDataException(ERROR_MESSAGE));
    }

    private <U> ResponseEntity<T> createHasBodyMethod(Supplier<WebClient.RequestBodyUriSpec> restMethod, String urn,
                                      List<QueryParam> queryParams,
                                      Map<String, List<String>> headers, U bodyValue,
                                      Class<T> responseEntity) {
        return restMethod.get()
            .uri(uriBuilder -> setQueryParams(uriBuilder.path(urn), queryParams).build())
            .headers(httpHeaders -> setHeaders(httpHeaders, headers))
            .bodyValue(bodyValue)
            .retrieve()
            .toEntity(responseEntity)
            .timeout(timeOut)
            .blockOptional()
            .orElseThrow(() -> new NoResponseDataException(ERROR_MESSAGE));
    }

    private UriBuilder setQueryParams(UriBuilder builder, List<QueryParam> queryParams) {
        if (Objects.nonNull(queryParams)) {
            log.debug("queryParams len : {}", queryParams.size());
            queryParams.forEach(queryParam ->
                builder.queryParam(queryParam.getName(), queryParam.getValues()));
        }

        return builder;
    }

    private void setHeaders(HttpHeaders httpHeaders, Map<String, List<String>> headers) {
        if (Objects.nonNull(headers)) {
            httpHeaders.putAll(headers);
        }
    }
}
