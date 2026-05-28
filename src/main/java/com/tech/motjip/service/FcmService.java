package com.tech.motjip.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmService {

    public void sendNotification(
            String targetToken,
            String title,
            String body
    ) {

        sendNotification(
                targetToken,
                title,
                body,
                null,
                null,
                null,
                null
        );
    }

    public void sendNotification(
            String targetToken,
            String title,
            String body,
            Long roomId,
            String roomName,
            String roomType,
            String type
    ) {

        if (targetToken == null
                || targetToken.trim().isEmpty()) {

            System.out.println(
                    "FCM 전송 생략 : targetToken 없음"
            );

            return;
        }

        try {

            Message.Builder builder =
                    Message.builder()
                            .setToken(
                                    targetToken.trim()
                            );

            builder.putData(
                    "title",
                    title != null
                            && !title.trim().isEmpty()
                            ? title.trim()
                            : "맛집"
            );

            builder.putData(
                    "body",
                    body != null
                            && !body.trim().isEmpty()
                            ? body.trim()
                            : "새 알림이 도착했습니다."
            );

            if (roomId != null) {

                builder.putData(
                        "roomId",
                        String.valueOf(roomId)
                );
            }

            putDataIfNotEmpty(
                    builder,
                    "roomName",
                    roomName
            );

            putDataIfNotEmpty(
                    builder,
                    "roomType",
                    roomType
            );

            putDataIfNotEmpty(
                    builder,
                    "type",
                    type
            );

            Message message =
                    builder.build();

            String response =
                    FirebaseMessaging.getInstance()
                            .send(message);

            System.out.println(
                    "FCM data-only 전송 성공 : "
                            + response
            );

        } catch (Exception e) {

            System.out.println(
                    "FCM data-only 전송 실패"
            );

            e.printStackTrace();
        }
    }

    private void putDataIfNotEmpty(
            Message.Builder builder,
            String key,
            String value
    ) {

        if (builder == null
                || key == null
                || key.trim().isEmpty()
                || value == null
                || value.trim().isEmpty()) {

            return;
        }

        builder.putData(
                key,
                value.trim()
        );
    }
}