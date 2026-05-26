package com.tech.motjip.dto.responseDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleTokenResponse {
    private String access_token;
    private String expires_in;
    private String token_type;
    private String scope;
    private String refresh_token;
}