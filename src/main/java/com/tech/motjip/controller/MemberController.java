package com.tech.motjip.controller;

import com.tech.motjip.dto.requestDto.NicknameUpdateRequestDto;
import com.tech.motjip.dto.requestDto.StatusUpdateRequestDto;
import com.tech.motjip.dto.requestDto.UpdateLocationRequestDto;
import com.tech.motjip.dto.requestDto.UpdateMyNicknameRequestDto;
import com.tech.motjip.dto.responseDto.LoginResponseDto;
import com.tech.motjip.service.MemberService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PatchMapping("/nickname")
    public ResponseEntity<LoginResponseDto> updateNickname(

            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String tokenHeader,

            @RequestBody
            NicknameUpdateRequestDto requestDto
    ) {

        if (tokenHeader == null ||
                !tokenHeader.startsWith("Bearer ")) {

            return ResponseEntity.status(401).build();
        }

        String token =
                tokenHeader.substring(7);

        LoginResponseDto response =
                memberService.updateNickname(
                        token,
                        requestDto
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/nickname")
    public ResponseEntity<LoginResponseDto> updateMyNickname(

            @RequestHeader(value = "Authorization")
            String tokenHeader,

            @RequestBody
            UpdateMyNicknameRequestDto requestDto
    ) {

        if (!tokenHeader.startsWith("Bearer ")) {

            return ResponseEntity.status(401).build();
        }

        String token =
                tokenHeader.substring(7);

        LoginResponseDto response =
                memberService.updateMyNickname(
                        token,
                        requestDto
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping(
            value = "/me/profile-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<LoginResponseDto> updateProfileImage(

            @RequestHeader(value = "Authorization")
            String tokenHeader,

            @RequestPart("image")
            MultipartFile image
    ) {

        if (!tokenHeader.startsWith("Bearer ")) {

            return ResponseEntity.status(401).build();
        }

        String token =
                tokenHeader.substring(7);

        LoginResponseDto response =
                memberService.updateProfileImage(
                        token,
                        image
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/status")
    public ResponseEntity<Void> updateMyStatus(

            @RequestHeader(value = "Authorization")
            String tokenHeader,

            @RequestBody
            StatusUpdateRequestDto requestDto
    ) {

        if (tokenHeader == null ||
                !tokenHeader.startsWith("Bearer ")) {

            return ResponseEntity.status(401).build();
        }

        String token =
                tokenHeader.substring(7);

        memberService.updateMyStatus(
                token,
                requestDto
        );

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/me/location")
    public ResponseEntity<Void> updateMyLocation(

            @RequestHeader(value = "Authorization")
            String tokenHeader,

            @RequestBody
            UpdateLocationRequestDto requestDto
    ) {

        if (tokenHeader == null ||
                !tokenHeader.startsWith("Bearer ")) {

            return ResponseEntity.status(401).build();
        }

        String token =
                tokenHeader.substring(7);

        memberService.updateMyLocation(
                token,
                requestDto
        );

        return ResponseEntity.ok().build();
    }
}