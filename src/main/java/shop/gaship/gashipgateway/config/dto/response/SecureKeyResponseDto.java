package shop.gaship.gashipgateway.config.dto.response;

import lombok.Getter;

/**
 * SecureKey에 대한 정보를 받고 다른 객체에 전달하기 위한 Dto입니다.
 *
 * @author 조재철
 * @since 1.0
 */
@Getter
public class SecureKeyResponseDto {

    private Header header;

    private Body body;

    /**
     * SecureKey Field 중 Header에 대한 클래스.
     *
     * @author 조재철
     * @since 1.0
     */
    @Getter
    public static class Header {
        private Integer resultCode;
        private String resultMessage;
        private Boolean isSuccessful;
    }

    /**
     * SecureKey에 Field 중 Body에 대한 클래스.
     *
     * @author 조재철
     * @since 1.0
     */
    @Getter
    public static class Body {
        private String secret;
    }
}

