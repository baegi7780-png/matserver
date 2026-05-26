package com.tech.motjip.dto.responseDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleUserInfoResponse {
    private String sub;
    private String email;
    private boolean email_verified;
    private String name;
}