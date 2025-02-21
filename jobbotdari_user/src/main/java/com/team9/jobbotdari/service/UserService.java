package com.team9.jobbotdari.service;

import com.team9.jobbotdari.common.jwt.JwtUtils;
import com.team9.jobbotdari.dto.request.SigninRequestDto;
import com.team9.jobbotdari.dto.request.SignupRequestDto;
import com.team9.jobbotdari.dto.response.SigninResponseDto;
import com.team9.jobbotdari.entity.User;
import com.team9.jobbotdari.entity.enums.Role;
import com.team9.jobbotdari.exception.signup.SignupException;
import com.team9.jobbotdari.repository.UserRepository;
import com.team9.jobbotdari.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FileService fileService;
    private final BCryptPasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Transactional
    public void registerUser(SignupRequestDto request, MultipartFile file) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new SignupException.DuplicateUsernameException();
        }

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new SignupException.PasswordMismatchException();
        }

        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .password(encryptedPassword)
                .role(Role.USER) // 기본적으로 USER 역할 부여
                .build();

        userRepository.save(user);

        // 파일이 있는 경우에만 저장
        if (file != null && !file.isEmpty()) {
            fileService.saveFile(file, user);
        }
    }

    public SigninResponseDto authenticateUser(SigninRequestDto request) {
        // Spring Security의 AuthenticationManager를 사용한 인증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 인증된 사용자 정보 가져오기
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // JWT 토큰 생성
        String accessToken = jwtUtils.generateToken(userDetails);

        return new SigninResponseDto(accessToken);
    }
}
