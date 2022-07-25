package shop.gaship.gashipgateway.config.exceptions;

/**
 * 응답 데이터를 받았는데 null이 들어온 경우 발생시킬 예외클래스 입니다.
 *
 * @author 조재철
 * @since 1.0
 */
public class NoResponseDataException extends RuntimeException {

    public NoResponseDataException(String message) {
        super(message);
    }
}
