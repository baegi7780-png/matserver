package com.tech.motjip.service;

import com.tech.motjip.domain.Friend;
import com.tech.motjip.domain.FriendRequest;
import com.tech.motjip.domain.FriendRequestStatus;
import com.tech.motjip.domain.Member;

import com.tech.motjip.dto.responseDto.FriendRecommendationResponseDto;
import com.tech.motjip.dto.responseDto.FriendResponseDto;

import com.tech.motjip.repository.FriendRepository;
import com.tech.motjip.repository.FriendRequestRepository;
import com.tech.motjip.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendService {

    private final FriendRequestRepository friendRequestRepository;

    private final FriendRepository friendRepository;

    private final MemberRepository memberRepository;

    private final NotificationService notificationService;

    private final FcmService fcmService;

    public void sendFriendRequest(
            String senderEmailId,
            Long receiverId
    ) {

        Member sender =
                memberRepository.findByEmailId(senderEmailId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "요청 보낸 사용자를 찾을 수 없습니다."
                                )
                        );

        Member receiver =
                memberRepository.findById(receiverId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "요청 받을 사용자를 찾을 수 없습니다."
                                )
                        );

        if (Boolean.TRUE.equals(
                receiver.getRejectFriendRequest()
        )) {

            throw new RuntimeException(
                    "상대방은 친구 초대 거부 상태입니다."
            );
        }

        if (sender.getMemberId().equals(
                receiver.getMemberId()
        )) {

            throw new RuntimeException(
                    "자기 자신에게는 친구 요청을 보낼 수 없습니다."
            );
        }

        boolean alreadyFriend =
                friendRepository
                        .existsByMemberAndFriendMember(
                                sender,
                                receiver
                        );

        if (alreadyFriend) {

            throw new RuntimeException(
                    "이미 친구인 사용자입니다."
            );
        }

        List<FriendRequestStatus> blockedStatus =
                List.of(
                        FriendRequestStatus.PENDING
                );

        boolean alreadyRequested =
                friendRequestRepository
                        .existsBySenderAndReceiverAndStatusIn(
                                sender,
                                receiver,
                                blockedStatus
                        );

        if (alreadyRequested) {

            throw new RuntimeException(
                    "이미 친구 요청을 보냈습니다."
            );
        }

        FriendRequest latestRequest =
                friendRequestRepository
                        .findTopBySenderAndReceiverOrderByCreatedAtDesc(
                                sender,
                                receiver
                        )
                        .orElse(null);

        if (latestRequest != null
                && (
                latestRequest.getStatus()
                        == FriendRequestStatus.REJECTED
                        || latestRequest.getStatus()
                        == FriendRequestStatus.CANCELED
        )) {

            LocalDateTime availableTime =
                    latestRequest.getCreatedAt()
                            .plusMinutes(5);

            if (LocalDateTime.now().isBefore(
                    availableTime
            )) {

                throw new RuntimeException(
                        "친구 삭제 또는 거절 후 5분 뒤 다시 요청할 수 있습니다."
                );
            }
        }

        FriendRequest friendRequest =
                new FriendRequest();

        friendRequest.setSender(
                sender
        );

        friendRequest.setReceiver(
                receiver
        );

        friendRequest.setStatus(
                FriendRequestStatus.PENDING
        );

        friendRequestRepository.save(
                friendRequest
        );

        notificationService.createNotification(
                receiver.getEmailId(),
                sender.getEmailId(),
                "FRIEND_INVITE",
                friendRequest.getFriendRequestId(),
                sender.getNickname()
                        + "님이 친구 요청을 보냈습니다."
        );

        System.out.println(
                "===== 친구 요청 푸시 테스트 ====="
        );

        System.out.println(
                "receiver : "
                        + receiver.getEmailId()
        );

        System.out.println(
                "token : "
                        + receiver.getFcmToken()
        );

        if (receiver.getFcmToken() != null
                && !receiver.getFcmToken().isBlank()) {

            System.out.println(
                    "FCM 전송 시작"
            );

            fcmService.sendNotification(
                    receiver.getFcmToken(),
                    "친구 요청",
                    sender.getNickname()
                            + "님이 친구 요청을 보냈습니다."
            );

        } else {

            System.out.println(
                    "FCM TOKEN 없음"
            );
        }
    }

    public void respondFriendRequest(
            String receiverEmailId,
            Long friendRequestId,
            String status
    ) {

        Member receiver =
                memberRepository.findByEmailId(
                                receiverEmailId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "사용자를 찾을 수 없습니다."
                                )
                        );

        FriendRequest friendRequest =
                friendRequestRepository.findById(
                                friendRequestId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "친구 요청을 찾을 수 없습니다."
                                )
                        );

        if (!friendRequest.getReceiver()
                .getMemberId()
                .equals(receiver.getMemberId())) {

            throw new RuntimeException(
                    "본인에게 온 친구 요청만 처리할 수 있습니다."
            );
        }

        if (friendRequest.getStatus()
                != FriendRequestStatus.PENDING) {

            throw new RuntimeException(
                    "이미 처리된 친구 요청입니다."
            );
        }

        Member sender =
                friendRequest.getSender();

        if ("ACCEPTED".equalsIgnoreCase(
                status
        )) {

            friendRequest.setStatus(
                    FriendRequestStatus.ACCEPTED
            );

            clearPendingRequests(
                    sender,
                    receiver
            );

            saveFriendRelation(
                    receiver,
                    sender
            );

            saveFriendRelation(
                    sender,
                    receiver
            );

            notificationService.updateStatusByTargetId(
                    friendRequestId,
                    "FRIEND_INVITE",
                    "ACCEPTED"
            );

        } else if ("REJECTED".equalsIgnoreCase(
                status
        )) {

            friendRequest.setStatus(
                    FriendRequestStatus.REJECTED
            );

            notificationService.updateStatusByTargetId(
                    friendRequestId,
                    "FRIEND_INVITE",
                    "REJECTED"
            );

        } else {

            throw new RuntimeException(
                    "잘못된 친구 요청 상태입니다."
            );
        }
    }

    public void cancelFriendRequest(
            String senderEmailId,
            Long friendRequestId
    ) {

        FriendRequest friendRequest =
                friendRequestRepository.findById(
                                friendRequestId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "친구 요청을 찾을 수 없습니다."
                                )
                        );

        if (!friendRequest.getSender()
                .getEmailId()
                .equals(senderEmailId)) {

            throw new RuntimeException(
                    "본인이 보낸 친구 요청만 취소할 수 있습니다."
            );
        }

        if (friendRequest.getStatus()
                != FriendRequestStatus.PENDING) {

            throw new RuntimeException(
                    "대기중인 친구 요청만 취소할 수 있습니다."
            );
        }

        friendRequest.setStatus(
                FriendRequestStatus.CANCELED
        );

        notificationService.updateStatusByTargetId(
                friendRequestId,
                "FRIEND_INVITE",
                "CANCELED"
        );
    }

    public String getFriendStatus(
            String myEmailId,
            Long targetMemberId
    ) {

        Member me =
                memberRepository.findByEmailId(
                                myEmailId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "사용자를 찾을 수 없습니다."
                                )
                        );

        Member target =
                memberRepository.findById(
                                targetMemberId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "대상 사용자를 찾을 수 없습니다."
                                )
                        );

        boolean isFriend =
                friendRepository
                        .existsByMemberAndFriendMember(
                                me,
                                target
                        );

        if (isFriend) {

            return "FRIEND";
        }

        FriendRequest sentRequest =
                friendRequestRepository
                        .findTopBySenderAndReceiverOrderByCreatedAtDesc(
                                me,
                                target
                        )
                        .orElse(null);

        if (sentRequest != null
                && sentRequest.getStatus()
                == FriendRequestStatus.PENDING) {

            return "PENDING";
        }

        FriendRequest receivedRequest =
                friendRequestRepository
                        .findTopByReceiverAndSenderOrderByCreatedAtDesc(
                                me,
                                target
                        )
                        .orElse(null);

        if (receivedRequest != null
                && receivedRequest.getStatus()
                == FriendRequestStatus.PENDING) {

            return "RECEIVED";
        }

        return "NONE";
    }

    @Transactional(readOnly = true)
    public List<FriendRecommendationResponseDto> getFriendRecommendations(
            String myEmailId
    ) {

        Member me =
                memberRepository.findByEmailId(
                                myEmailId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "사용자를 찾을 수 없습니다."
                                )
                        );

        if (me.getLatitude() == null
                || me.getLongitude() == null) {

            System.out.println(
                    "===== 친구 추천 실패 ====="
            );

            System.out.println(
                    "내 위치 정보가 없습니다."
            );

            return List.of();
        }

        List<Member> members =
                memberRepository.findByLocationEnabledTrue();

        System.out.println(
                "===== 친구 추천 시작 ====="
        );

        System.out.println(
                "me = " + me.getEmailId()
        );

        System.out.println(
                "lat = " + me.getLatitude()
        );

        System.out.println(
                "lng = " + me.getLongitude()
        );

        System.out.println(
                "추천 대상 수 = " + members.size()
        );

        return members.stream()
                .filter(target ->
                        !target.getMemberId()
                                .equals(me.getMemberId())
                )
                .filter(target ->
                        target.getLatitude() != null
                                && target.getLongitude() != null
                )
                .filter(target ->
                        !Boolean.TRUE.equals(
                                target.getRejectFriendRecommend()
                        )
                )
                .map(target -> {

                    double distanceKm =
                            calculateDistanceKm(
                                    me.getLatitude(),
                                    me.getLongitude(),
                                    target.getLatitude(),
                                    target.getLongitude()
                            );

                    String friendStatus =
                            getFriendStatus(
                                    myEmailId,
                                    target.getMemberId()
                            );

                    System.out.println(
                            "추천 후보 = "
                                    + target.getEmailId()
                    );

                    System.out.println(
                            "target lat = "
                                    + target.getLatitude()
                    );

                    System.out.println(
                            "target lng = "
                                    + target.getLongitude()
                    );

                    System.out.println(
                            "distanceKm = "
                                    + distanceKm
                    );

                    System.out.println(
                            "status = "
                                    + friendStatus
                    );

                    Long friendRequestId = null;

                    if ("RECEIVED".equals(friendStatus)) {

                        FriendRequest receivedRequest =
                                friendRequestRepository
                                        .findTopByReceiverAndSenderOrderByCreatedAtDesc(
                                                me,
                                                target
                                        )
                                        .orElse(null);

                        if (receivedRequest != null) {

                            friendRequestId =
                                    receivedRequest.getFriendRequestId();
                        }
                    }

                    return FriendRecommendationResponseDto.builder()
                            .memberId(target.getMemberId())
                            .nickname(target.getNickname())
                            .profileImgUrl(target.getProfileImgUrl())
                            .distanceKm(distanceKm)
                            .status(friendStatus)
                            .friendRequestId(friendRequestId)
                            .build();
                })
                .filter(dto -> {

                    boolean isInDistance =
                            dto.getDistanceKm() <= 1.0;

                    if (!isInDistance) {

                        System.out.println(
                                "추천 제외 - 1km 초과 : "
                                        + dto.getNickname()
                                        + " / "
                                        + dto.getDistanceKm()
                                        + "km"
                        );
                    }

                    return isInDistance;
                })
                .filter(dto -> {

                    boolean canShow =
                            !"FRIEND".equals(dto.getStatus())
                                    && !"RECEIVED".equals(dto.getStatus());

                    if (!canShow) {

                        System.out.println(
                                "추천 제외 - 상태 : "
                                        + dto.getNickname()
                                        + " / "
                                        + dto.getStatus()
                        );
                    }

                    return canShow;
                })
                .sorted(
                        Comparator.comparing(
                                FriendRecommendationResponseDto::getDistanceKm
                        )
                )
                .collect(Collectors.toList());
    }

    public List<FriendResponseDto> getMyFriends(
            String memberEmailId
    ) {

        Member member =
                memberRepository.findByEmailId(
                                memberEmailId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "사용자를 찾을 수 없습니다."
                                )
                        );

        List<Friend> friends =
                friendRepository.findByMember(
                        member
                );

        return friends.stream()
                .map(friend -> {

                    Member friendMember =
                            friend.getFriendMember();

                    return new FriendResponseDto(
                            friendMember.getMemberId(),
                            friendMember.getNickname(),
                            friendMember.getProfileImgUrl()
                    );
                })
                .collect(Collectors.toList());
    }

    private void clearPendingRequests(
            Member member1,
            Member member2
    ) {

        List<FriendRequest> requests =
                friendRequestRepository.findBetweenMembersByStatus(
                        member1,
                        member2,
                        FriendRequestStatus.PENDING
                );

        for (FriendRequest request : requests) {

            request.setStatus(
                    FriendRequestStatus.ACCEPTED
            );
        }
    }

    private void saveFriendRelation(
            Member member,
            Member friendMember
    ) {

        boolean exists =
                friendRepository
                        .existsByMemberAndFriendMember(
                                member,
                                friendMember
                        );

        if (exists) {

            return;
        }

        Friend friend =
                new Friend();

        friend.setMember(
                member
        );

        friend.setFriendMember(
                friendMember
        );

        friendRepository.save(
                friend
        );
    }

    public void deleteFriend(
            String memberEmailId,
            Long friendMemberId
    ) {

        Member member =
                memberRepository.findByEmailId(
                                memberEmailId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "사용자를 찾을 수 없습니다."
                                )
                        );

        Member friendMember =
                memberRepository.findById(
                                friendMemberId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "친구 사용자를 찾을 수 없습니다."
                                )
                        );

        friendRepository
                .findByMemberAndFriendMember(
                        member,
                        friendMember
                )
                .ifPresent(
                        friendRepository::delete
                );

        friendRepository
                .findByMemberAndFriendMember(
                        friendMember,
                        member
                )
                .ifPresent(
                        friendRepository::delete
                );

        List<FriendRequest> requests =
                friendRequestRepository
                        .findBySenderAndReceiverOrSenderAndReceiver(
                                member,
                                friendMember,
                                friendMember,
                                member
                        );

        for (FriendRequest request : requests) {

            request.setStatus(
                    FriendRequestStatus.CANCELED
            );
        }
    }

    private double calculateDistanceKm(
            double lat1,
            double lon1,
            double lat2,
            double lon2
    ) {

        final int earthRadiusKm = 6371;

        double dLat =
                Math.toRadians(
                        lat2 - lat1
                );

        double dLon =
                Math.toRadians(
                        lon2 - lon1
                );

        double a =
                Math.sin(dLat / 2)
                        * Math.sin(dLat / 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);

        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                );

        return earthRadiusKm * c;
    }
}