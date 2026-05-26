package com.tech.motjip.dto.responseDto;

import java.util.List;
import java.util.Map;

public class ChatReadEventDto {

    private Long roomId;

    private Long readerId;

    private String type;

    private List<Long> readMessageIds;

    private Map<Long, Integer> unreadCountMap;

    public ChatReadEventDto() {
    }

    public ChatReadEventDto(
            Long roomId,
            Long readerId,
            String type,
            List<Long> readMessageIds,
            Map<Long, Integer> unreadCountMap
    ) {

        this.roomId = roomId;
        this.readerId = readerId;
        this.type = type;
        this.readMessageIds = readMessageIds;
        this.unreadCountMap = unreadCountMap;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getReaderId() {
        return readerId;
    }

    public void setReaderId(Long readerId) {
        this.readerId = readerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Long> getReadMessageIds() {
        return readMessageIds;
    }

    public void setReadMessageIds(List<Long> readMessageIds) {
        this.readMessageIds = readMessageIds;
    }

    public Map<Long, Integer> getUnreadCountMap() {
        return unreadCountMap;
    }

    public void setUnreadCountMap(Map<Long, Integer> unreadCountMap) {
        this.unreadCountMap = unreadCountMap;
    }
}