package shop.gaship.gashipgateway.token.store;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 설명작성란
 *
 * @author 조재철
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public class Payload {
    private Long identificationNumber;
    private List<String> role;
}