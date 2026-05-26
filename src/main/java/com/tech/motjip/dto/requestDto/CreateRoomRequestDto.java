package com.tech.motjip.dto.requestDto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateRoomRequestDto {

    private String roomName;

    // DIRECT or GROUP
    private String roomType;

    // 참여자 목록
    private List<Long> memberIds;


}