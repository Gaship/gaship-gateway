package shop.gaship.gashipgateway.token.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import java.security.Key;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * packageName    : shop.gaship.gashipgateway.token.util fileName       : JwtTokenUtil author
 *  : jo date           : 2022/07/15 description    : ===========================================================
 * DATE              AUTHOR             NOTE -----------------------------------------------------------
 * 2022/07/15        jo       최초 생성
 */
@RequiredArgsConstructor
@Component
public class JwtTokenUtil {

    private final Key createKey;

    public Date getExpiredDate(String token) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(createKey).parseClaimsJws(token);

        return claims.getBody().getExpiration();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(createKey).parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public int getUserNum(String token) {
        return Integer.parseInt(
            Jwts.parserBuilder().setSigningKey(createKey).build().parseClaimsJws(token).getBody()
                .getSubject());
    }
}
