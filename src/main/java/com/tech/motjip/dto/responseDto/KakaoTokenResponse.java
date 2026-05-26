package com.tech.motjip.dto.responseDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoTokenResponse {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private int expires_in;
    private String scope;
}