package com.tech.motjip.controller;

import com.tech.motjip.dto.responseDto.UploadResponseDto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @PostMapping("/upload")
    public ResponseEntity<UploadResponseDto> uploadFile(
            @RequestPart("file") MultipartFile file
    ) {

        try {

            if (file == null
                    || file.isEmpty()) {

                return ResponseEntity.badRequest()
                        .build();
            }

            String uploadDir =
                    System.getProperty("user.dir")
                            + "/uploads/chat/";

            File directory =
                    new File(uploadDir);

            if (!directory.exists()) {

                directory.mkdirs();
            }

            String originalFilename =
                    file.getOriginalFilename();

            String extension = "";

            if (originalFilename != null
                    && originalFilename.contains(".")) {

                extension =
                        originalFilename.substring(
                                originalFilename.lastIndexOf(".")
                        );
            }

            String savedFileName =
                    UUID.randomUUID()
                            + extension;

            File destination =
                    new File(
                            uploadDir
                                    + savedFileName
                    );

            file.transferTo(destination);

            String fileUrl =
                    "/uploads/chat/"
                            + savedFileName;

            UploadResponseDto response =
                    new UploadResponseDto(
                            fileUrl
                    );

            return ResponseEntity.ok(
                    response
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError()
                    .build();
        }
    }
}