package shop.gaship.gashipgateway.token.store;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Jwt의 payload에 담기는 정보를 가지는 클래스.
 *
 * @author 조재철
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public class Payload {

    private Long identificationNumber;
    private String role;
}