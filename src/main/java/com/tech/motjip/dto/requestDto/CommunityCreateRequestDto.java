package com.tech.motjip.dto.requestDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommunityCreateRequestDto {

    private String tag;

    private String region;

    private String title;

    private String location;

    private String date;

    // 채팅방 링크
    // 선택 입력 가능
    private String chatLink;

    private String content;

    public CommunityCreateRequestDto(
            String tag,
            String region,
            String title,
            String location,
            String date,
            String chatLink,
            String content
    ) {

        this.tag = tag;
        this.region = region;
        this.title = title;
        this.location = location;
        this.date = date;
        this.chatLink = chatLink;
        this.content = content;
    }
}