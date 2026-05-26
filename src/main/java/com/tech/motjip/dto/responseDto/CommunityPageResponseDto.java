package com.tech.motjip.dto.responseDto;

import java.util.List;

public class CommunityPageResponseDto {

    private List<CommunityListResponseDto> content;

    private int page;

    private int size;

    private boolean last;

    public CommunityPageResponseDto(
            List<CommunityListResponseDto> content,
            int page,
            int size,
            boolean last
    ) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.last = last;
    }

    public List<CommunityListResponseDto> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public boolean isLast() {
        return last;
    }
}