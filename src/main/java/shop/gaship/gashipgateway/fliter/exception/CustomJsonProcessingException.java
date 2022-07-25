package shop.gaship.gashipgateway.fliter.exception;

/**
 * Json 타입 String으로 변환 중 발생한 에러에 관한 class 입니다.
 *
 * @author : 조재철
 * @since 1.0
 */
public class CustomJsonProcessingException extends RuntimeException {

    public CustomJsonProcessingException(String message) {
        super(message);
    }
}
