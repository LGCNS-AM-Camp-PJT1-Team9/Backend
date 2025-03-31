package com.team9.jobbotdari.service;

import com.team9.jobbotdari.dto.request.AddCompanyRequestDto;
import com.team9.jobbotdari.dto.response.BaseResponseDto;
import com.team9.jobbotdari.dto.response.LogListResponseDto;
import com.team9.jobbotdari.dto.response.UserListResponseDto;
import com.team9.jobbotdari.entity.Log;
import com.team9.jobbotdari.entity.User;
import com.team9.jobbotdari.exception.user.UserNotFoundException;
import com.team9.jobbotdari.repository.LogRepository;
import com.team9.jobbotdari.repository.UserRepository;
import com.team9.jobbotdari.security.CustomUserDetails;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    final int PAGE_SIZE = 30;

    private final UserRepository userRepository;
    private final LogRepository logRepository;

    private final JobbotariFeignClient jobbotariFeignClient;

    private static final ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<UserListResponseDto> getUserList() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new UserNotFoundException();
        }

        return userRepository.findAll().stream()
                .filter(user -> !currentUser.getId().equals(user.getId()))
                .map(user -> modelMapper.map(user, UserListResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<LogListResponseDto> getLogs() {
        LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);
        List<Log> allLogs = new ArrayList<>();
        int pageNum = 0;
        Page<Log> logPage;

        do {
            Pageable pageable = PageRequest.of(pageNum, PAGE_SIZE);
            logPage = logRepository.findByCreatedAtAfter(todayStart, pageable);
            allLogs.addAll(logPage.getContent());
            pageNum++;
        } while (logPage.hasNext());

        return allLogs.stream()
                .map(log -> LogListResponseDto.builder()
                        .userId(log.getUser() != null ? log.getUser().getId() : null)
                        .name(log.getUser() != null ? log.getUser().getName() : null)
                        .description(log.getDescription())
                        .action(log.getAction())
                        .createdAt(log.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }


    @Retry(name = "adminService", fallbackMethod = "fallbackMethod")
    @CircuitBreaker(name = "adminService", fallbackMethod = "fallbackMethod")
    @Override
    public BaseResponseDto addCompany(AddCompanyRequestDto addCompanyRequestDto) {
       return jobbotariFeignClient.addCompany(addCompanyRequestDto);
    }

    public String fallbackMethod() {
        return "Fallback response: Company API를 사용할 수 없습니다.";
    }

    private User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                return ((CustomUserDetails) authentication.getPrincipal()).getUser();
            }
        } catch (Exception e) {
            log.warn("User 추출 실패: {}", e.getMessage());
        }
        return null;
    }
}
