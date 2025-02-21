package com.team9.jobbotdari.service;

import com.team9.jobbotdari.dto.response.ProfileResponseDto;
import com.team9.jobbotdari.entity.File;
import com.team9.jobbotdari.entity.User;
import com.team9.jobbotdari.exception.user.UserNotFoundException;
import com.team9.jobbotdari.repository.FileRepository;
import com.team9.jobbotdari.repository.UserRepository;
import com.team9.jobbotdari.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    @Value("${file.access-url}")
    private String fileAccessUrl;

    public ProfileResponseDto getProfile(CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(UserNotFoundException::new);

        File file = fileRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId()).orElse(null);

        String fileUrl = (file != null) ? fileAccessUrl + file.getFilename() : null;

        return new ProfileResponseDto(
                user.getId(),
                user.getName(),
                user.getUsername(),
                fileUrl
        );
    }
}