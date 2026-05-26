package com.tech.motjip.dto.requestDto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeleteNotificationRequestDto {

    private List<Long> notificationIds;
}