package com.tech.motjip.service;

import com.tech.motjip.domain.Community;
import com.tech.motjip.domain.Favorite;
import com.tech.motjip.domain.Member;

import com.tech.motjip.dto.responseDto.CommunityListResponseDto;

import com.tech.motjip.repository.CommunityRepository;
import com.tech.motjip.repository.FavoriteRepository;
import com.tech.motjip.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    private final MemberRepository memberRepository;

    private final CommunityRepository communityRepository;

    private final CommunityQueryService
            communityQueryService;

    @Transactional
    public boolean toggleFavoriteCommunityPost(
            String emailId,
            Long comId
    ) {

        Member member =
                memberRepository.findByEmailId(emailId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "회원 정보를 찾을 수 없습니다."
                                )
                        );

        Community community =
                communityRepository.findById(comId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "게시글을 찾을 수 없습니다."
                                )
                        );

        Optional<Favorite> favoriteOptional =
                favoriteRepository
                        .findByMember_MemberIdAndCommunity_ComId(
                                member.getMemberId(),
                                community.getComId()
                        );

        if (favoriteOptional.isPresent()) {

            favoriteRepository.delete(
                    favoriteOptional.get()
            );

            return false;
        }

        Favorite favorite =
                new Favorite(
                        member,
                        community
                );

        favoriteRepository.save(
                favorite
        );

        return true;
    }

    @Transactional(readOnly = true)
    public List<CommunityListResponseDto> getFavoriteCommunityPosts(
            String emailId
    ) {

        return favoriteRepository
                .findAllByMember_EmailIdOrderByFavoriteIdDesc(
                        emailId
                )
                .stream()
                .map(favorite ->
                        communityQueryService
                                .convertToResponseDto(
                                        favorite.getCommunity(),
                                        emailId,
                                        false
                                )
                )
                .collect(Collectors.toList());
    }
}