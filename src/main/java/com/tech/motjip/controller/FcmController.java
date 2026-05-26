package com.tech.motjip.controller;

import com.tech.motjip.service.FcmService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FcmController {

    private final FcmService fcmService;

    @GetMapping("/fcm/test")
    public String sendTestNotification(
            @RequestParam String token
    ) {

        fcmService.sendNotification(
                token,
                "맛집 알림",
                "푸시 알림 테스트 성공!"
        );

        return "FCM 전송 완료";
    }
}