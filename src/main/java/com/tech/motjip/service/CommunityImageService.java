package com.tech.motjip.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service
public class CommunityImageService {

    public String saveCommunityImage(
            MultipartFile image
    ) {

        if (image == null || image.isEmpty()) {
            return null;
        }

        try {

            String uploadDir =
                    System.getProperty("user.dir")
                            + "/uploads/community/";

            File directory =
                    new File(uploadDir);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            String originalFilename =
                    image.getOriginalFilename();

            String extension = ".jpg";

            if (originalFilename != null
                    && originalFilename.contains(".")) {

                extension =
                        originalFilename.substring(
                                originalFilename.lastIndexOf(".")
                        );
            }

            String savedFileName =
                    UUID.randomUUID() + extension;

            File destination =
                    new File(uploadDir + savedFileName);

            image.transferTo(destination);

            return "/uploads/community/" + savedFileName;

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "커뮤니티 이미지 저장 실패"
            );
        }
    }
}