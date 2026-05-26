package com.tech.motjip.service;

import com.tech.motjip.domain.Community;
import com.tech.motjip.domain.CommunityMember;
import com.tech.motjip.domain.FriendRequest;
import com.tech.motjip.domain.FriendRequestStatus;
import com.tech.motjip.domain.Member;

import com.tech.motjip.dto.responseDto.CommunityMemberResponseDto;

import com.tech.motjip.repository.CommunityMemberRepository;
import com.tech.motjip.repository.CommunityRepository;
import com.tech.motjip.repository.FriendRepository;
import com.tech.motjip.repository.FriendRequestRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityMemberService {

    private final CommunityRepository communityRepository;

    private final CommunityMemberRepository communityMemberRepository;

    private final FriendRepository friendRepository;

    private final FriendRequestRepository friendRequestRepository;

    public void joinCommunity(
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

        if (community.getMeetingAt().isBefore(
                LocalDateTime.now()
        )) {

            throw new IllegalArgumentException(
                    "이미 종료된 모임입니다."
            );
        }

        Optional<CommunityMember> optionalMember =
                communityMemberRepository
                        .findByCommunityAndMember(
                                community,
                                member
                        );

        if (optionalMember.isPresent()) {

            CommunityMember communityMember =
                    optionalMember.get();

            if ("JOINED".equals(
                    communityMember.getStatus()
            )) {

                throw new IllegalArgumentException(
                        "이미 참여한 모임입니다."
                );
            }

            if ("KICKED".equals(
                    communityMember.getStatus()
            )) {

                throw new IllegalArgumentException(
                        "추방된 모임에는 다시 참여할 수 없습니다."
                );
            }

            communityMember.setStatus(
                    "JOINED"
            );

            communityMember.setHidden(false);

            communityMemberRepository.save(
                    communityMember
            );

            return;
        }

        CommunityMember communityMember =
                CommunityMember.builder()
                        .community(community)
                        .member(member)
                        .role("MEMBER")
                        .status("JOINED")
                        .build();

        communityMember.setHidden(false);

        communityMemberRepository.save(
                communityMember
        );
    }

    public void cancelJoinCommunity(
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

        CommunityMember communityMember =
                communityMemberRepository
                        .findByCommunityAndMember(
                                community,
                                member
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "참여한 모임이 아닙니다."
                                )
                        );

        if ("HOST".equals(
                communityMember.getRole()
        )) {

            throw new IllegalArgumentException(
                    "모임장은 참여 취소할 수 없습니다. 게시글 삭제를 이용해 주세요."
            );
        }

        if ("CANCEL".equals(
                communityMember.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "이미 참여 취소한 모임입니다."
            );
        }

        if ("KICKED".equals(
                communityMember.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "이미 추방된 모임입니다."
            );
        }

        communityMember.setStatus(
                "CANCEL"
        );

        communityMemberRepository.save(
                communityMember
        );
    }

    public void kickMember(
            Long comId,
            Long targetMemberId,
            Member hostMember
    ) {

        Community community =
                communityRepository.findById(comId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "존재하지 않는 게시글입니다."
                                )
                        );

        CommunityMember hostCommunityMember =
                communityMemberRepository
                        .findByCommunityAndMember(
                                community,
                                hostMember
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "모임 참여 정보가 없습니다."
                                )
                        );

        if (!"HOST".equals(
                hostCommunityMember.getRole()
        )) {

            throw new IllegalArgumentException(
                    "모임장만 추방할 수 있습니다."
            );
        }

        CommunityMember targetCommunityMember =
                communityMemberRepository
                        .findByCommunity_ComIdAndMember_MemberId(
                                comId,
                                targetMemberId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "추방할 참여자를 찾을 수 없습니다."
                                )
                        );

        if ("HOST".equals(
                targetCommunityMember.getRole()
        )) {

            throw new IllegalArgumentException(
                    "모임장은 추방할 수 없습니다."
            );
        }

        if (!"JOINED".equals(
                targetCommunityMember.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "현재 참여 중인 사용자만 추방할 수 있습니다."
            );
        }

        targetCommunityMember.setStatus(
                "KICKED"
        );

        communityMemberRepository.save(
                targetCommunityMember
        );
    }

    public void hideMyClosedCommunity(
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

        if (!community.getMeetingAt().isBefore(
                LocalDateTime.now()
        )) {

            throw new IllegalArgumentException(
                    "종료된 모임만 숨길 수 있습니다."
            );
        }

        CommunityMember communityMember =
                communityMemberRepository
                        .findByCommunityAndMember(
                                community,
                                member
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "참여한 모임이 아닙니다."
                                )
                        );

        if ("HOST".equals(
                communityMember.getRole()
        )) {

            throw new IllegalArgumentException(
                    "모임장은 숨김이 아닌 삭제를 이용해 주세요."
            );
        }

        if (!"JOINED".equals(
                communityMember.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "참여 중인 모임만 숨길 수 있습니다."
            );
        }

        communityMember.setHidden(true);

        communityMemberRepository.save(
                communityMember
        );
    }

    @Transactional(readOnly = true)
    public List<CommunityMemberResponseDto> getCommunityMembers(
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

        CommunityMember requester =
                communityMemberRepository
                        .findByCommunityAndMember(
                                community,
                                member
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "모임 참여자만 참여자 목록을 볼 수 있습니다."
                                )
                        );

        if (!"JOINED".equals(
                requester.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "모임 참여자만 참여자 목록을 볼 수 있습니다."
            );
        }

        return communityMemberRepository
                .findByCommunityAndStatus(
                        community,
                        "JOINED"
                )
                .stream()
                .map(communityMember -> {

                    Member targetMember =
                            communityMember.getMember();

                    boolean friend =
                            friendRepository
                                    .existsByMemberAndFriendMember(
                                            member,
                                            targetMember
                                    );

                    String friendStatus =
                            "NONE";

                    if (friend) {

                        friendStatus =
                                "FRIEND";

                    } else {

                        FriendRequest sentRequest =
                                friendRequestRepository
                                        .findTopBySenderAndReceiverOrderByCreatedAtDesc(
                                                member,
                                                targetMember
                                        )
                                        .orElse(null);

                        if (sentRequest != null
                                && sentRequest.getStatus()
                                == FriendRequestStatus.PENDING) {

                            friendStatus =
                                    "PENDING";
                        }

                        FriendRequest receivedRequest =
                                friendRequestRepository
                                        .findTopByReceiverAndSenderOrderByCreatedAtDesc(
                                                member,
                                                targetMember
                                        )
                                        .orElse(null);

                        if (receivedRequest != null
                                && receivedRequest.getStatus()
                                == FriendRequestStatus.PENDING) {

                            friendStatus =
                                    "RECEIVED";
                        }
                    }

                    CommunityMemberResponseDto dto =
                            CommunityMemberResponseDto.from(
                                    communityMember,
                                    friend
                            );

                    dto.setFriendStatus(
                            friendStatus
                    );

                    return dto;
                })
                .collect(Collectors.toList());
    }
}