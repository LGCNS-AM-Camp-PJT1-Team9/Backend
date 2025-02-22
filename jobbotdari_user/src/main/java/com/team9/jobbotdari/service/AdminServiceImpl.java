package com.team9.jobbotdari.service;

import com.team9.jobbotdari.dto.response.LogListResponseDto;
import com.team9.jobbotdari.dto.response.UserListResponseDto;
import com.team9.jobbotdari.entity.Log;
import com.team9.jobbotdari.exception.user.UserNotFoundException;
import com.team9.jobbotdari.repository.LogRepository;
import com.team9.jobbotdari.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final LogRepository logRepository;

    private static final ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<UserListResponseDto> getUserList() {
        return userRepository.findAll().stream()
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
        List<Log> logs = logRepository.findAll();

        return logs.stream()
                // 사용자별 로그 그룹화
                .collect(Collectors.groupingBy(log -> log.getUser().getId()))
                .entrySet().stream()
                .map(entry -> {
                    Long userId = entry.getKey();
                    List<Log> userLogs = entry.getValue();
                    String userName = userLogs.get(0).getUser().getName();  // 사용자 이름

                    // Log toString() 으로 변환하고, List에 담음
                    List<String> logStrings = userLogs.stream()
                            .map(Log::toString)
                            .collect(Collectors.toList());

                    return LogListResponseDto.builder()
                            .userId(userId)
                            .name(userName)
                            .log(logStrings)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
