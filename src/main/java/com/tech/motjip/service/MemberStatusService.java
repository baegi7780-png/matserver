package com.tech.motjip.service;

import com.tech.motjip.domain.Member;
import com.tech.motjip.dto.requestDto.UpdateMyStatusSettingRequestDto;
import com.tech.motjip.dto.responseDto.MyStatusSettingResponseDto;
import com.tech.motjip.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberStatusService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MyStatusSettingResponseDto getMyStatusSetting(
            String email
    ) {

        Member member = memberRepository.findByEmailId(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "회원을 찾을 수 없습니다."
                        )
                );

        return MyStatusSettingResponseDto.from(member);
    }

    @Transactional
    public MyStatusSettingResponseDto updateMyStatusSetting(
            String email,
            UpdateMyStatusSettingRequestDto requestDto
    ) {

        Member member = memberRepository.findByEmailId(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "회원을 찾을 수 없습니다."
                        )
                );

        member.updateMyStatusSettings(
                requestDto.getRejectFriendRequest(),
                requestDto.getRejectChat(),
                requestDto.getRejectFriendRecommend(),
                requestDto.getRejectCommunityInvite()
        );

        return MyStatusSettingResponseDto.from(member);
    }
}