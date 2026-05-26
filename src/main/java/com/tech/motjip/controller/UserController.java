package com.tech.motjip.controller;

import com.tech.motjip.dto.responseDto.LoginResponseDto;
import com.tech.motjip.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<LoginResponseDto> getMyInfo(Authentication authentication) {
        String email = authentication.getName(); // JWT에서 꺼낸 email

        LoginResponseDto response = memberService.getMyInfo(email);

        return ResponseEntity.ok(response);
    }
}