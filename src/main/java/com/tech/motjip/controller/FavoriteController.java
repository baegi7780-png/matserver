package com.tech.motjip.controller;

import com.tech.motjip.dto.responseDto.CommunityListResponseDto;
import com.tech.motjip.service.FavoriteService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/community/{comId}/toggle")
    public ResponseEntity<Boolean> toggleFavoriteCommunityPost(
            @PathVariable Long comId,
            Authentication authentication
    ) {

        String emailId =
                authentication.getName();

        boolean isFavorite =
                favoriteService.toggleFavoriteCommunityPost(
                        emailId,
                        comId
                );

        return ResponseEntity.ok(isFavorite);
    }

    @GetMapping("/community")
    public ResponseEntity<List<CommunityListResponseDto>> getFavoriteCommunityPosts(
            Authentication authentication
    ) {

        String emailId =
                authentication.getName();

        List<CommunityListResponseDto> response =
                favoriteService.getFavoriteCommunityPosts(
                        emailId
                );

        return ResponseEntity.ok(response);
    }
}