package com.tech.motjip.service;

import com.tech.motjip.domain.Community;
import com.tech.motjip.domain.CommunityInvite;
import com.tech.motjip.domain.CommunityInviteStatus;
import com.tech.motjip.domain.CommunityMember;
import com.tech.motjip.domain.Member;

import com.tech.motjip.repository.CommunityInviteRepository;
import com.tech.motjip.repository.CommunityMemberRepository;
import com.tech.motjip.repository.CommunityRepository;
import com.tech.motjip.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityInviteService {

    private final CommunityInviteRepository communityInviteRepository;

    private final CommunityRepository communityRepository;

    private final CommunityMemberRepository communityMemberRepository;

    private final MemberRepository memberRepository;

    private final NotificationService notificationService;

    private final FcmService fcmService;

    public void sendCommunityInvite(
            String senderEmailId,
            Long communityId,
            Long receiverId
    ) {

        Member sender =
                memberRepository.findByEmailId(senderEmailId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "초대 보낸 사용자를 찾을 수 없습니다."
                                )
                        );

        Member receiver =
                memberRepository.findById(receiverId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "초대 받을 사용자를 찾을 수 없습니다."
                                )
                        );

        if (Boolean.TRUE.equals(
                receiver.getRejectCommunityInvite()
        )) {

            throw new RuntimeException(
                    "상대방은 모임 초대 거부 상태입니다."
            );
        }

        Community community =
                communityRepository.findById(communityId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "모임을 찾을 수 없습니다."
                                )
                        );

        if (sender.getMemberId().equals(
                receiver.getMemberId()
        )) {

            throw new RuntimeException(
                    "자기 자신은 초대할 수 없습니다."
            );
        }

        if (community.getIsDeleted() != null
                && community.getIsDeleted()) {

            throw new RuntimeException(
                    "삭제된 모임에는 초대할 수 없습니다."
            );
        }

        boolean senderIsMember =
                communityMemberRepository
                        .existsByCommunityAndMemberAndStatus(
                                community,
                                sender,
                                "JOINED"
                        );

        if (!senderIsMember) {

            throw new RuntimeException(
                    "모임 참여자만 초대할 수 있습니다."
            );
        }

        boolean alreadyJoined =
                communityMemberRepository
                        .existsByCommunityAndMemberAndStatus(
                                community,
                                receiver,
                                "JOINED"
                        );

        if (alreadyJoined) {

            throw new RuntimeException(
                    "이미 모임에 참여 중인 사용자입니다."
            );
        }

        boolean alreadyPendingInvite =
                communityInviteRepository
                        .existsByCommunityAndSenderAndReceiverAndStatus(
                                community,
                                sender,
                                receiver,
                                CommunityInviteStatus.PENDING
                        );

        if (alreadyPendingInvite) {

            throw new RuntimeException(
                    "이미 모임 초대를 보냈습니다."
            );
        }

        CommunityInvite communityInvite =
                new CommunityInvite();

        communityInvite.setCommunity(
                community
        );

        communityInvite.setSender(
                sender
        );

        communityInvite.setReceiver(
                receiver
        );

        communityInvite.setStatus(
                CommunityInviteStatus.PENDING
        );

        communityInviteRepository.save(
                communityInvite
        );

        notificationService.createNotification(
                receiver.getEmailId(),
                sender.getEmailId(),
                "COMMUNITY_INVITE",
                communityInvite.getCommunityInviteId(),
                sender.getNickname()
                        + "님이 ["
                        + community.getTitle()
                        + "] 모임에 초대했습니다."
        );

        if (receiver.getFcmToken() != null
                && !receiver.getFcmToken().isBlank()) {

            fcmService.sendNotification(
                    receiver.getFcmToken(),
                    "모임 초대",
                    sender.getNickname()
                            + "님이 ["
                            + community.getTitle()
                            + "] 모임에 초대했습니다."
            );
        }
    }

    public void respondCommunityInvite(
            String receiverEmailId,
            Long communityInviteId,
            String status
    ) {

        Member receiver =
                memberRepository.findByEmailId(receiverEmailId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "사용자를 찾을 수 없습니다."
                                )
                        );

        CommunityInvite communityInvite =
                communityInviteRepository.findById(communityInviteId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "모임 초대를 찾을 수 없습니다."
                                )
                        );

        if (!communityInvite.getReceiver()
                .getMemberId()
                .equals(receiver.getMemberId())) {

            throw new RuntimeException(
                    "본인에게 온 모임 초대만 처리할 수 있습니다."
            );
        }

        if (communityInvite.getStatus()
                != CommunityInviteStatus.PENDING) {

            throw new RuntimeException(
                    "이미 처리된 모임 초대입니다."
            );
        }

        Community community =
                communityInvite.getCommunity();

        if ("ACCEPTED".equalsIgnoreCase(status)) {

            boolean alreadyJoined =
                    communityMemberRepository
                            .existsByCommunityAndMemberAndStatus(
                                    community,
                                    receiver,
                                    "JOINED"
                            );

            if (!alreadyJoined) {

                CommunityMember communityMember =
                        CommunityMember.builder()
                                .community(community)
                                .member(receiver)
                                .role("MEMBER")
                                .status("JOINED")
                                .build();

                communityMemberRepository.save(
                        communityMember
                );
            }

            communityInvite.setStatus(
                    CommunityInviteStatus.ACCEPTED
            );

            notificationService.updateStatusByTargetId(
                    communityInviteId,
                    "COMMUNITY_INVITE",
                    "ACCEPTED"
            );

        } else if ("REJECTED".equalsIgnoreCase(status)) {

            communityInvite.setStatus(
                    CommunityInviteStatus.REJECTED
            );

            notificationService.updateStatusByTargetId(
                    communityInviteId,
                    "COMMUNITY_INVITE",
                    "REJECTED"
            );

        } else {

            throw new RuntimeException(
                    "잘못된 모임 초대 상태입니다."
            );
        }
    }

    public void cancelCommunityInvite(
            String senderEmailId,
            Long communityInviteId
    ) {

        CommunityInvite communityInvite =
                communityInviteRepository.findById(communityInviteId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "모임 초대를 찾을 수 없습니다."
                                )
                        );

        if (!communityInvite.getSender()
                .getEmailId()
                .equals(senderEmailId)) {

            throw new RuntimeException(
                    "본인이 보낸 모임 초대만 취소할 수 있습니다."
            );
        }

        if (communityInvite.getStatus()
                != CommunityInviteStatus.PENDING) {

            throw new RuntimeException(
                    "대기중인 모임 초대만 취소할 수 있습니다."
            );
        }

        communityInvite.setStatus(
                CommunityInviteStatus.CANCELED
        );

        notificationService.updateStatusByTargetId(
                communityInviteId,
                "COMMUNITY_INVITE",
                "CANCELED"
        );
    }
}