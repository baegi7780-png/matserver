package com.tech.motjip.dto.responseDto;

import com.tech.motjip.domain.Community;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommunityListResponseDto {

    private Long comId;

    private String tag;

    private String title;

    private String content;

    private String region;

    private String placeName;

    private String meetingAt;

    private String imageUrl;

    // 채팅방 링크
    private String chatLink;

    private String writerNickname;

    private String createdAt;

    private boolean favorite;

    private boolean mine;

    private boolean joined;

    private boolean closed;

    private int memberCount;

    public static CommunityListResponseDto from(
            Community community
    ) {

        return CommunityListResponseDto.builder()
                .comId(community.getComId())
                .tag(community.getTag().getTagName())
                .title(community.getTitle())
                .content(community.getContent())
                .region(community.getRegion())
                .placeName(community.getPlaceName())

                .meetingAt(
                        community.getMeetingAt() != null
                                ? community.getMeetingAt().format(
                                java.time.format.DateTimeFormatter.ofPattern(
                                        "yyyy.MM.dd a h:mm",
                                        java.util.Locale.KOREAN
                                )
                        )
                                : null
                )

                .imageUrl(community.getImageUrl())

                // 채팅방 링크 추가
                .chatLink(
                        community.getChatLink()
                )

                .writerNickname(
                        community.getMember().getNickname()
                )

                .createdAt(
                        community.getCreatedAt() != null
                                ? community.getCreatedAt().toString()
                                : null
                )

                .favorite(false)

                .mine(false)

                .joined(false)

                .closed(
                        community.getMeetingAt() != null
                                && community.getMeetingAt().isBefore(
                                java.time.LocalDateTime.now()
                        )
                )

                .memberCount(0)

                .build();
    }

    public static CommunityListResponseDto from(
            Community community,
            boolean favorite,
            boolean mine
    ) {

        return CommunityListResponseDto.builder()
                .comId(community.getComId())
                .tag(community.getTag().getTagName())
                .title(community.getTitle())
                .content(community.getContent())
                .region(community.getRegion())
                .placeName(community.getPlaceName())

                .meetingAt(
                        community.getMeetingAt() != null
                                ? community.getMeetingAt().format(
                                java.time.format.DateTimeFormatter.ofPattern(
                                        "yyyy.MM.dd a h:mm",
                                        java.util.Locale.KOREAN
                                )
                        )
                                : null
                )

                .imageUrl(community.getImageUrl())

                // 채팅방 링크 추가
                .chatLink(
                        community.getChatLink()
                )

                .writerNickname(
                        community.getMember().getNickname()
                )

                .createdAt(
                        community.getCreatedAt() != null
                                ? community.getCreatedAt().toString()
                                : null
                )

                .favorite(favorite)

                .mine(mine)

                .joined(false)

                .closed(
                        community.getMeetingAt() != null
                                && community.getMeetingAt().isBefore(
                                java.time.LocalDateTime.now()
                        )
                )

                .memberCount(0)

                .build();
    }

    public static CommunityListResponseDto from(
            Community community,
            boolean favorite,
            boolean mine,
            boolean joined,
            int memberCount
    ) {

        return CommunityListResponseDto.builder()
                .comId(community.getComId())
                .tag(community.getTag().getTagName())
                .title(community.getTitle())
                .content(community.getContent())
                .region(community.getRegion())
                .placeName(community.getPlaceName())

                .meetingAt(
                        community.getMeetingAt() != null
                                ? community.getMeetingAt().format(
                                java.time.format.DateTimeFormatter.ofPattern(
                                        "yyyy.MM.dd a h:mm",
                                        java.util.Locale.KOREAN
                                )
                        )
                                : null
                )

                .imageUrl(community.getImageUrl())

                // 채팅방 링크 추가
                .chatLink(
                        community.getChatLink()
                )

                .writerNickname(
                        community.getMember().getNickname()
                )

                .createdAt(
                        community.getCreatedAt() != null
                                ? community.getCreatedAt().toString()
                                : null
                )

                .favorite(favorite)

                .mine(mine)

                .joined(joined)

                .closed(
                        community.getMeetingAt() != null
                                && community.getMeetingAt().isBefore(
                                java.time.LocalDateTime.now()
                        )
                )

                .memberCount(memberCount)

                .build();
    }
}