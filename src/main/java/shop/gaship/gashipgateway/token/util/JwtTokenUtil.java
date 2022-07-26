package shop.gaship.gashipgateway.token.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import java.security.Key;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.gaship.gashipgateway.token.store.Payload;

/**
 * 설명작성란
 *
 * @author 조재철
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    private final Key createKey;

    private Jws<Claims> getClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(createKey)
            .build()
            .parseClaimsJws(token);
    }

    /**
     * Jwt에서 Payload 정보를 가져오기 위한 메서드.
     *
     * @param token the token
     * @return the payload from token
     */
    public Payload getPayloadFromToken(String token) {
        Long identificationNumber = getClaims(token).getBody().get("id", Long.class);
        List<String> role = getClaims(token).getBody().get("role", List.class);
        return new Payload(identificationNumber, role);
    }
}
