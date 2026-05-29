package com.tech.motjip.dto.requestDto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateRoomRequestDto {

    // 사용자가 직접 입력한 채팅방 이름
    // null 또는 빈 문자열이면 참여자 닉네임 기반 동적 제목 사용
    private String roomName;

    // DIRECT or GROUP
    private String roomType;

    // 참여자 목록
    private List<Long> memberIds;
}