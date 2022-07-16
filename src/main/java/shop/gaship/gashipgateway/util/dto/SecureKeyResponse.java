package shop.gaship.gashipgateway.util.dto;

import lombok.Getter;

/**
 * packageName    : shop.gaship.gashipgateway.util.dto fileName       : SecureKeyResponse author
 *     : jo date           : 2022/07/15 description    : ===========================================================
 * DATE              AUTHOR             NOTE -----------------------------------------------------------
 * 2022/07/15        jo       최초 생성
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

