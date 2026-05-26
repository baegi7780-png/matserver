package com.tech.motjip.service;

import com.tech.motjip.domain.Community;

import com.tech.motjip.dto.responseDto.CommunityListResponseDto;
import com.tech.motjip.dto.responseDto.CommunityPageResponseDto;

import com.tech.motjip.repository.CommunityMemberRepository;
import com.tech.motjip.repository.CommunityRepository;
import com.tech.motjip.repository.FavoriteRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityQueryService {

    private final CommunityRepository communityRepository;

    private final FavoriteRepository favoriteRepository;

    private final CommunityMemberRepository
            communityMemberRepository;

    public CommunityPageResponseDto getCommunities(
            String emailId,
            String title,
            String tag,
            String region,
            String sort,
            int page,
            int size
    ) {

        Sort sortOption =
                getSortOption(sort);

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sortOption
                );

        Page<Community> communityPage =
                communityRepository.searchCommunities(
                        emailId,
                        title,
                        tag,
                        region,
                        LocalDateTime.now(),
                        pageable
                );

        List<CommunityListResponseDto> content =
                communityPage.getContent()
                        .stream()
                        .map(community ->
                                convertToResponseDto(
                                        community,
                                        emailId,
                                        false
                                )
                        )
                        .collect(Collectors.toList());

        return new CommunityPageResponseDto(
                content,
                communityPage.getNumber(),
                communityPage.getSize(),
                communityPage.isLast()
        );
    }

    public CommunityPageResponseDto getClosedCommunities(
            String emailId,
            String title,
            String tag,
            String region,
            String sort,
            int page,
            int size
    ) {

        Sort sortOption =
                getSortOption(sort);

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sortOption
                );

        Page<Community> communityPage =
                communityRepository.searchClosedCommunities(
                        emailId,
                        title,
                        tag,
                        region,
                        LocalDateTime.now(),
                        pageable
                );

        List<CommunityListResponseDto> content =
                communityPage.getContent()
                        .stream()
                        .map(community ->
                                convertToResponseDto(
                                        community,
                                        emailId,
                                        false
                                )
                        )
                        .collect(Collectors.toList());

        return new CommunityPageResponseDto(
                content,
                communityPage.getNumber(),
                communityPage.getSize(),
                communityPage.isLast()
        );
    }

    public CommunityPageResponseDto getMyCommunities(
            String emailId,
            String sort,
            int page,
            int size
    ) {

        Sort sortOption =
                getSortOption(sort);

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sortOption
                );

        Page<Community> myCreatedPage =
                communityRepository.findMyCommunities(
                        emailId,
                        LocalDateTime.now(),
                        pageable
                );

        Page<Community> myJoinedPage =
                communityRepository.findMyJoinedCommunities(
                        emailId,
                        LocalDateTime.now(),
                        pageable
                );

        LinkedHashMap<Long, Community> mergedMap =
                new LinkedHashMap<>();

        for (Community community : myCreatedPage.getContent()) {

            mergedMap.put(
                    community.getComId(),
                    community
            );
        }

        for (Community community : myJoinedPage.getContent()) {

            mergedMap.put(
                    community.getComId(),
                    community
            );
        }

        List<Community> mergedCommunities =
                new ArrayList<>(
                        mergedMap.values()
                );

        if ("old".equals(sort)) {

            mergedCommunities.sort(
                    Comparator.comparing(
                            Community::getCreatedAt
                    )
            );

        } else {

            mergedCommunities.sort(
                    Comparator.comparing(
                            Community::getCreatedAt
                    ).reversed()
            );
        }

        List<CommunityListResponseDto> content =
                mergedCommunities
                        .stream()
                        .map(community -> {

                            boolean mine =
                                    community.getMember()
                                            .getEmailId()
                                            .equals(emailId);

                            return convertToResponseDto(
                                    community,
                                    emailId,
                                    mine
                            );
                        })
                        .collect(Collectors.toList());

        boolean isLast =
                myCreatedPage.isLast()
                        && myJoinedPage.isLast();

        return new CommunityPageResponseDto(
                content,
                page,
                size,
                isLast
        );
    }

    public CommunityPageResponseDto getMyClosedCommunities(
            String emailId,
            String sort,
            int page,
            int size
    ) {

        Sort sortOption =
                getSortOption(sort);

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sortOption
                );

        Page<Community> myCreatedClosedPage =
                communityRepository.findMyClosedCommunities(
                        emailId,
                        LocalDateTime.now(),
                        pageable
                );

        Page<Community> myJoinedClosedPage =
                communityRepository.findMyJoinedClosedCommunities(
                        emailId,
                        LocalDateTime.now(),
                        pageable
                );

        LinkedHashMap<Long, Community> mergedMap =
                new LinkedHashMap<>();

        for (Community community : myCreatedClosedPage.getContent()) {

            mergedMap.put(
                    community.getComId(),
                    community
            );
        }

        for (Community community : myJoinedClosedPage.getContent()) {

            mergedMap.put(
                    community.getComId(),
                    community
            );
        }

        List<Community> mergedCommunities =
                new ArrayList<>(
                        mergedMap.values()
                );

        if ("old".equals(sort)) {

            mergedCommunities.sort(
                    Comparator.comparing(
                            Community::getCreatedAt
                    )
            );

        } else {

            mergedCommunities.sort(
                    Comparator.comparing(
                            Community::getCreatedAt
                    ).reversed()
            );
        }

        List<CommunityListResponseDto> content =
                mergedCommunities
                        .stream()
                        .map(community -> {

                            boolean mine =
                                    community.getMember()
                                            .getEmailId()
                                            .equals(emailId);

                            return convertToResponseDto(
                                    community,
                                    emailId,
                                    mine
                            );
                        })
                        .collect(Collectors.toList());

        boolean isLast =
                myCreatedClosedPage.isLast()
                        && myJoinedClosedPage.isLast();

        return new CommunityPageResponseDto(
                content,
                page,
                size,
                isLast
        );
    }

    public CommunityListResponseDto convertToResponseDto(
            Community community,
            String emailId,
            boolean forceMine
    ) {

        boolean favorite = false;

        boolean mine = forceMine;

        boolean joined = false;

        int memberCount =
                communityMemberRepository
                        .countByCommunity_ComIdAndStatus(
                                community.getComId(),
                                "JOINED"
                        );

        if (emailId != null
                && !emailId.trim().isEmpty()) {

            favorite =
                    favoriteRepository
                            .existsByMember_EmailIdAndCommunity_ComId(
                                    emailId,
                                    community.getComId()
                            );

            if (!forceMine) {

                mine =
                        community.getMember()
                                .getEmailId()
                                .equals(emailId);
            }

            joined =
                    communityMemberRepository
                            .existsByCommunity_ComIdAndMember_EmailIdAndStatus(
                                    community.getComId(),
                                    emailId,
                                    "JOINED"
                            );
        }

        return CommunityListResponseDto.from(
                community,
                favorite,
                mine,
                joined,
                memberCount
        );
    }

    private Sort getSortOption(
            String sort
    ) {

        if ("old".equals(sort)) {

            return Sort.by(
                    Sort.Direction.ASC,
                    "createdAt"
            );
        }

        return Sort.by(
                Sort.Direction.DESC,
                "createdAt"
        );
    }
}