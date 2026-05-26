package com.tech.motjip.service;

import com.tech.motjip.dto.requestDto.CommunityCreateRequestDto;

import org.springframework.stereotype.Component;

@Component
public class CommunityValidator {

    public void validateCommunityRequest(
            CommunityCreateRequestDto requestDto
    ) {

        if (requestDto.getTitle() == null
                || requestDto.getTitle().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "제목을 입력해 주세요."
            );
        }

        if (requestDto.getTitle().length() > 20) {

            throw new IllegalArgumentException(
                    "제목은 20자 이하만 가능합니다."
            );
        }

        if (requestDto.getContent() == null
                || requestDto.getContent().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "내용을 입력해 주세요."
            );
        }

        if (requestDto.getContent().length() > 500) {

            throw new IllegalArgumentException(
                    "내용은 500자 이하만 가능합니다."
            );
        }
    }
}