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

        try {

            Message.Builder builder =
                    Message.builder()
                            .setToken(
                                    targetToken
                            );

            builder.putData(
                    "title",
                    title != null
                            ? title
                            : "맛집"
            );

            builder.putData(
                    "body",
                    body != null
                            ? body
                            : "새 알림이 도착했습니다."
            );

            if (roomId != null) {

                builder.putData(
                        "roomId",
                        String.valueOf(
                                roomId
                        )
                );
            }

            if (roomName != null
                    && !roomName.trim().isEmpty()) {

                builder.putData(
                        "roomName",
                        roomName
                );
            }

            if (roomType != null
                    && !roomType.trim().isEmpty()) {

                builder.putData(
                        "roomType",
                        roomType
                );
            }

            if (type != null
                    && !type.trim().isEmpty()) {

                builder.putData(
                        "type",
                        type
                );
            }

            Message message =
                    builder.build();

            String response =
                    FirebaseMessaging.getInstance()
                            .send(
                                    message
                            );

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
}