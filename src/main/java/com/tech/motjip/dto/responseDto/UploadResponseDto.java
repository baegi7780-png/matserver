package com.tech.motjip.dto.responseDto;


public class UploadResponseDto {

    private String fileUrl;

    public UploadResponseDto() {
    }

    public UploadResponseDto(
            String fileUrl
    ) {

        this.fileUrl =
                fileUrl;
    }

    public String getFileUrl() {

        return fileUrl;
    }

    public void setFileUrl(
            String fileUrl
    ) {

        this.fileUrl =
                fileUrl;
    }
}