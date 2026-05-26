package com.tech.motjip.dto.requestDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequestDto {
    private String email;
    private Integer providerId; // 1: 카카오, 2: 구글
}