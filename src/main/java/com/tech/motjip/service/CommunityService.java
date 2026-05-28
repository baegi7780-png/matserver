package com.tech.motjip.service;

import com.tech.motjip.domain.Community;
import com.tech.motjip.domain.CommunityMember;
import com.tech.motjip.domain.Member;
import com.tech.motjip.domain.Tag;

import com.tech.motjip.dto.requestDto.CommunityCreateRequestDto;

import com.tech.motjip.repository.CommunityMemberRepository;
import com.tech.motjip.repository.CommunityRepository;
import com.tech.motjip.repository.TagRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityService {

    private final CommunityRepository communityRepository;

    private final CommunityMemberRepository communityMemberRepository;

    private final TagRepository tagRepository;

    private final CommunityImageService communityImageService;

    private final CommunityValidator communityValidator;

    public void createCommunity(
            CommunityCreateRequestDto requestDto,
            Member member,
            MultipartFile image
    ) {

        communityValidator.validateCommunityRequest(
                requestDto
        );

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd HH:mm"
                );

        LocalDateTime meetingAt =
                LocalDateTime.parse(
                        requestDto.getDate(),
                        formatter
                );

        String imageUrl =
                communityImageService.saveCommunityImage(
                        image
                );

        Tag tag =
                tagRepository.findByTagName(requestDto.getTag())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "존재하지 않는 태그입니다: "
                                                + requestDto.getTag()
                                )
                        );

        Community community =
                Community.builder()
                        .boardTypeId(1)
                        .member(member)
                        .tag(tag)
                        .title(requestDto.getTitle())
                        .content(requestDto.getContent())
                        .region(requestDto.getRegion())
                        .placeName(requestDto.getLocation())
                        .meetingAt(meetingAt)
                        .imageUrl(imageUrl)

                        // 채팅방 링크
                        // 선택 입력 가능
                        .chatLink(
                                requestDto.getChatLink()
                        )

                        .build();

        Community savedCommunity =
                communityRepository.save(
                        community
                );

        CommunityMember communityMember =
                CommunityMember.builder()
                        .community(savedCommunity)
                        .member(member)
                        .role("HOST")
                        .status("JOINED")
                        .build();

        communityMemberRepository.save(
                communityMember
        );
    }

    public void updateCommunity(
            Long comId,
            CommunityCreateRequestDto requestDto,
            Member member,
            MultipartFile image
    ) {

        communityValidator.validateCommunityRequest(
                requestDto
        );

        Community community =
                communityRepository.findById(comId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "존재하지 않는 게시글입니다."
                                )
                        );

        if (!community.getMember()
                .getMemberId()
                .equals(member.getMemberId())) {

            throw new IllegalArgumentException(
                    "수정 권한이 없습니다."
            );
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd HH:mm"
                );

        LocalDateTime meetingAt =
                LocalDateTime.parse(
                        requestDto.getDate(),
                        formatter
                );

        Tag tag =
                tagRepository.findByTagName(requestDto.getTag())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "존재하지 않는 태그입니다: "
                                                + requestDto.getTag()
                                )
                        );

        community.setTag(tag);

        community.setTitle(
                requestDto.getTitle()
        );

        community.setContent(
                requestDto.getContent()
        );

        community.setRegion(
                requestDto.getRegion()
        );

        community.setPlaceName(
                requestDto.getLocation()
        );

        community.setMeetingAt(
                meetingAt
        );

        // 채팅방 링크 수정
        community.setChatLink(
                requestDto.getChatLink()
        );

        if (image != null && !image.isEmpty()) {

            String imageUrl =
                    communityImageService.saveCommunityImage(
                            image
                    );

            community.setImageUrl(imageUrl);
        }

        communityRepository.save(
                community
        );
    }

    @Transactional
    public void deleteCommunity(
            Long comId,
            Member member
    ) {

        Community community =
                communityRepository.findById(comId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "존재하지 않는 게시글입니다."
                                )
                        );

        if (!community.getMember()
                .getMemberId()
                .equals(member.getMemberId())) {

            throw new IllegalArgumentException(
                    "삭제 권한이 없습니다."
            );
        }

        communityRepository.delete(
                community
        );
    }
}