package com.tech.motjip.dto.requestDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LogoutRequestDto {

    private String refreshToken;
}