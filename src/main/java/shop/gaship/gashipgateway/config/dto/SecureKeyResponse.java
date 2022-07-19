package shop.gaship.gashipgateway.config.dto;

import lombok.Getter;

/**
 * 설명작성란
 *
 * @author 조재철
 * @since 1.0
 */
@Getter
public class SecureKeyResponse {

    private Header header;

    private Body body;

    @Getter
    public static class Header {
        private Integer resultCode;
        private String resultMessage;
        private Boolean isSuccessful;
    }

    @Getter
    public static class Body {
        private String secret;
    }
}

