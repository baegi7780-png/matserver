package com.tech.motjip.controller;

import com.tech.motjip.dto.requestDto.UpdateMyStatusSettingRequestDto;
import com.tech.motjip.dto.responseDto.MyStatusSettingResponseDto;
import com.tech.motjip.service.MemberStatusService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/member-status")
@RequiredArgsConstructor
public class MemberStatusController {

    private final MemberStatusService memberStatusService;

    @GetMapping
    public ResponseEntity<MyStatusSettingResponseDto> getMyStatusSetting(
            Authentication authentication
    ) {

        String email = authentication.getName();

        MyStatusSettingResponseDto responseDto =
                memberStatusService.getMyStatusSetting(email);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping
    public ResponseEntity<MyStatusSettingResponseDto> updateMyStatusSetting(
            @RequestBody UpdateMyStatusSettingRequestDto requestDto,
            Authentication authentication
    ) {

        String email = authentication.getName();

        MyStatusSettingResponseDto responseDto =
                memberStatusService.updateMyStatusSetting(
                        email,
                        requestDto
                );

        return ResponseEntity.ok(responseDto);
    }
}