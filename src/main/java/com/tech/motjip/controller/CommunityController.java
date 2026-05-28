package com.tech.motjip.controller;

import com.tech.motjip.domain.Member;

import com.tech.motjip.dto.requestDto.CommunityCreateRequestDto;
import com.tech.motjip.dto.responseDto.CommunityMemberResponseDto;
import com.tech.motjip.dto.responseDto.CommunityPageResponseDto;

import com.tech.motjip.repository.MemberRepository;

import com.tech.motjip.service.CommunityMemberService;
import com.tech.motjip.service.CommunityQueryService;
import com.tech.motjip.service.CommunityService;

import com.tech.motjip.util.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community/posts")
public class CommunityController {

    private final CommunityService communityService;

    private final CommunityQueryService
            communityQueryService;

    private final CommunityMemberService
            communityMemberService;

    private final MemberRepository memberRepository;

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<Void> createCommunity(

            @RequestHeader("Authorization")
            String authorizationHeader,

            @RequestPart("tag")
            String tag,

            @RequestPart("region")
            String region,

            @RequestPart("title")
            String title,

            @RequestPart("location")
            String location,

            @RequestPart("date")
            String date,

            @RequestPart(
                    value = "chatLink",
                    required = false
            )
            String chatLink,

            @RequestPart("content")
            String content,

            @RequestPart(value = "image", required = false)
            MultipartFile image
    ) {

        String token =
                authorizationHeader.replace(
                        "Bearer ",
                        ""
                );

        String email =
                jwtTokenProvider.getSubjectFromToken(
                        token
                );

        Member member =
                memberRepository.findByEmailId(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "사용자를 찾을 수 없습니다."
                                )
                        );

        CommunityCreateRequestDto requestDto =
                new CommunityCreateRequestDto(
                        tag,
                        region,
                        title,
                        location,
                        date,
                        chatLink,
                        content
                );

        if (title.length() > 25) {

            throw new IllegalArgumentException(
                    "제목은 최대 25자까지 입력 가능합니다."
            );
        }

        if (content.length() > 500) {

            throw new IllegalArgumentException(
                    "내용은 최대 500자까지 입력 가능합니다."
            );
        }

        communityService.createCommunity(
                requestDto,
                member,
                image
        );

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{comId}")
    public ResponseEntity<Void> updateCommunity(

            @RequestHeader("Authorization")
            String authorizationHeader,

            @PathVariable
            Long comId,

            @RequestPart("tag")
            String tag,

            @RequestPart("region")
            String region,

            @RequestPart("title")
            String title,

            @RequestPart("location")
            String location,

            @RequestPart("date")
            String date,

            @RequestPart(
                    value = "chatLink",
                    required = false
            )
            String chatLink,

            @RequestPart("content")
            String content,

            @RequestPart(value = "image", required = false)
            MultipartFile image
    ) {

        String token =
                authorizationHeader.replace(
                        "Bearer ",
                        ""
                );

        String email =
                jwtTokenProvider.getSubjectFromToken(
                        token
                );

        Member member =
                memberRepository.findByEmailId(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "사용자를 찾을 수 없습니다."
                                )
                        );

        CommunityCreateRequestDto requestDto =
                new CommunityCreateRequestDto(
                        tag,
                        region,
                        title,
                        location,
                        date,
                        chatLink,
                        content
                );

        if (title.length() > 25) {

            throw new IllegalArgumentException(
                    "제목은 최대 25자까지 입력 가능합니다."
            );
        }

        if (content.length() > 500) {

            throw new IllegalArgumentException(
                    "내용은 최대 500자까지 입력 가능합니다."
            );
        }

        communityService.updateCommunity(
                comId,
                requestDto,
                member,
                image
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<CommunityPageResponseDto>
    getCommunities(

            @RequestHeader("Authorization")
            String authorizationHeader,

            @RequestParam(
                    value = "title",
                    required = false
            )
            String title,

            @RequestParam(
                    value = "tag",
                    required = false
            )
            String tag,

            @RequestParam(
                    value = "region",
                    required = false
            )
            String region,

            @RequestParam(
                    value = "sort",
                    required = false,
                    defaultValue = "new"
            )
            String sort,

            @RequestParam(
                    value = "page",
                    required = false,
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    value = "size",
                    required = false,
                    defaultValue = "10"
            )
            int size
    ) {

        String token =
                authorizationHeader.replace(
                        "Bearer ",
                        ""
                );

        String email =
                jwtTokenProvider.getSubjectFromToken(
                        token
                );

        CommunityPageResponseDto communities =
                communityQueryService.getCommunities(
                        email,
                        title,
                        tag,
                        region,
                        sort,
                        page,
                        size
                );

        return ResponseEntity.ok(
                communities
        );
    }

    @GetMapping("/closed")
    public ResponseEntity<CommunityPageResponseDto>
    getClosedCommunities(

            @RequestHeader("Authorization")
            String authorizationHeader,

            @RequestParam(
                    value = "title",
                    required = false
            )
            String title,

            @RequestParam(
                    value = "tag",
                    required = false
            )
            String tag,

            @RequestParam(
                    value = "region",
                    required = false
            )
            String region,

            @RequestParam(
                    value = "sort",
                    required = false,
                    defaultValue = "new"
            )
            String sort,

            @RequestParam(
                    value = "page",
                    required = false,
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    value = "size",
                    required = false,
                    defaultValue = "10"
            )
            int size
    ) {

        String token =
                authorizationHeader.replace(
                        "Bearer ",
                        ""
                );

        String email =
                jwtTokenProvider.getSubjectFromToken(
                        token
                );

        CommunityPageResponseDto communities =
                communityQueryService.getClosedCommunities(
                        email,
                        title,
                        tag,
                        region,
                        sort,
                        page,
                        size
                );

        return ResponseEntity.ok(
                communities
        );
    }

    @GetMapping("/my")
    public ResponseEntity<CommunityPageResponseDto>
    getMyCommunities(

            @RequestHeader("Authorization")
            String authorizationHeader,

            @RequestParam(
                    value = "sort",
                    required = false,
                    defaultValue = "new"
            )
            String sort,

            @RequestParam(
                    value = "page",
                    required = false,
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    value = "size",
                    required = false,
                    defaultValue = "10"
            )
            int size
    ) {

        String token =
                authorizationHeader.replace(
                        "Bearer ",
                        ""
                );

        String email =
                jwtTokenProvider.getSubjectFromToken(
                        token
                );

        CommunityPageResponseDto communities =
                communityQueryService.getMyCommunities(
                        email,
                        sort,
                        page,
                        size
                );

        return ResponseEntity.ok(
                communities
        );
    }

    @GetMapping("/my/closed")
    public ResponseEntity<CommunityPageResponseDto>
    getMyClosedCommunities(

            @RequestHeader("Authorization")
            String authorizationHeader,

            @RequestParam(
                    value = "sort",
                    required = false,
                    defaultValue = "new"
            )
            String sort,

            @RequestParam(
                    value = "page",
                    required = false,
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    value = "size",
                    required = false,
                    defaultValue = "10"
            )
            int size
    ) {

        String token =
                authorizationHeader.replace(
                        "Bearer ",
                        ""
                );

        String email =
                jwtTokenProvider.getSubjectFromToken(
                        token
                );

        CommunityPageResponseDto communities =
                communityQueryService.getMyClosedCommunities(
                        email,
                        sort,
                        page,
                        size
                );

        return ResponseEntity.ok(
                communities
        );
    }

    @GetMapping("/{comId}/members")
    public ResponseEntity<List<CommunityMemberResponseDto>>
    getCommunityMembers(

            @RequestHeader("Authorization")
            String authorizationHeader,

            @PathVariable
            Long comId
    ) {

        String token =
                authorizationHeader.replace(
                        "Bearer ",
                        ""
                );

        String email =
                jwtTokenProvider.getSubjectFromToken(
                        token
                );

        Member member =
                memberRepository.findByEmailId(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "사용자를 찾을 수 없습니다."
                                )
                        );

        List<CommunityMemberResponseDto> members =
                communityMemberService.getCommunityMembers(
                        comId,
                        member
                );

        return ResponseEntity.ok(
                members
        );
    }

    @PostMapping("/{comId}/join")
    public ResponseEntity<Void> joinCommunity(

            @RequestHeader("Authorization")
            String authorizationHeader,

            @PathVariable
            Long comId
    ) {

        String token =
                authorizationHeader.replace(
                        "Bearer ",
                        ""
                );

        String email =
                jwtTokenProvider.getSubjectFromToken(
                        token
                );

        Member member =
                memberRepository.findByEmailId(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "사용자를 찾을 수 없습니다."
                                )
                        );

        communityMemberService.joinCommunity(
                comId,
                member
        );

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{comId}/cancel")
    public ResponseEntity<Void> cancelJoinCommunity(

            @RequestHeader("Authorization")
            String authorizationHeader,

            @PathVariable
            Long comId
    ) {

        String token =
                authorizationHeader.replace(
                        "Bearer ",
                        ""
                );

        String email =
                jwtTokenProvider.getSubjectFromToken(
                        token
                );

        Member member =
                memberRepository.findByEmailId(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "사용자를 찾을 수 없습니다."
                                )
                        );

        communityMemberService.cancelJoinCommunity(
                comId,
                member
        );

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{comId}/hide")
    public ResponseEntity<Void> hideMyClosedCommunity(

            @RequestHeader("Authorization")
            String authorizationHeader,

            @PathVariable
            Long comId
    ) {

        String token =
                authorizationHeader.replace(
                        "Bearer ",
                        ""
                );

        String email =
                jwtTokenProvider.getSubjectFromToken(
                        token
                );

        Member member =
                memberRepository.findByEmailId(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "사용자를 찾을 수 없습니다."
                                )
                        );

        communityMemberService.hideMyClosedCommunity(
                comId,
                member
        );

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{comId}/members/{targetMemberId}/kick")
    public ResponseEntity<Void> kickMember(

            @RequestHeader("Authorization")
            String authorizationHeader,

            @PathVariable
            Long comId,

            @PathVariable
            Long targetMemberId
    ) {

        String token =
                authorizationHeader.replace(
                        "Bearer ",
                        ""
                );

        String email =
                jwtTokenProvider.getSubjectFromToken(
                        token
                );

        Member member =
                memberRepository.findByEmailId(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "사용자를 찾을 수 없습니다."
                                )
                        );

        communityMemberService.kickMember(
                comId,
                targetMemberId,
                member
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{comId}")
    public ResponseEntity<Void> deleteCommunity(

            @RequestHeader("Authorization")
            String authorizationHeader,

            @PathVariable
            Long comId
    ) {

        String token =
                authorizationHeader.replace(
                        "Bearer ",
                        ""
                );

        String email =
                jwtTokenProvider.getSubjectFromToken(
                        token
                );

        Member member =
                memberRepository.findByEmailId(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "사용자를 찾을 수 없습니다."
                                )
                        );

        communityService.deleteCommunity(
                comId,
                member
        );

        return ResponseEntity.ok().build();
    }
}