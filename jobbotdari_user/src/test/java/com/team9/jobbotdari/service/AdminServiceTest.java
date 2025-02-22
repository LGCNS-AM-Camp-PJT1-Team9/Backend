package com.team9.jobbotdari.service;

import com.team9.jobbotdari.dto.response.LogListResponseDto;
import com.team9.jobbotdari.entity.Log;
import com.team9.jobbotdari.entity.User;
import com.team9.jobbotdari.repository.LogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @InjectMocks
    private AdminServiceImpl adminService;

    @Mock
    private LogRepository logRepository;

    @Test
    public void testGetLogs_GroupingAndMapping() {
        // Arrange: 두 명의 사용자와 해당 사용자의 로그 생성
        User user1 = User.builder()
                .id(1L)
                .username("alice")
                .name("Alice")
                .build();

        User user2 = User.builder()
                .id(2L)
                .username("bob")
                .name("Bob")
                .build();

        LocalDateTime time1 = LocalDateTime.of(2025, 2, 22, 9, 0, 0);
        LocalDateTime time2 = LocalDateTime.of(2025, 2, 22, 10, 0, 0);

        Log log1 = Log.builder()
                .id(100L)
                .user(user1)
                .action("login")
                .description("Login success")
                .createdAt(time1)
                .build();

        Log log2 = Log.builder()
                .id(101L)
                .user(user1)
                .action("update")
                .description("Profile updated")
                .createdAt(time2)
                .build();

        Log log3 = Log.builder()
                .id(102L)
                .user(user2)
                .action("login")
                .description("Login success")
                .createdAt(time1)
                .build();

        // logRepository.findAll()가 생성한 로그 리스트를 반환하도록 설정
        when(logRepository.findAll()).thenReturn(Arrays.asList(log1, log2, log3));

        // Act: Service의 getLogs() 메서드 호출
        List<LogListResponseDto> result = adminService.getLogs();

        // Assert: 결과 검증
        assertNotNull(result);
        // 사용자 1과 사용자 2의 로그가 각각 DTO에 그룹화되어 2개의 DTO가 반환되어야 함
        assertEquals(2, result.size());

        // DTO 리스트를 사용자 ID를 키로 하는 Map으로 변환하여 검증 용이하게 함
        Map<Long, LogListResponseDto> dtoMap = result.stream()
                .collect(Collectors.toMap(LogListResponseDto::getUserId, dto -> dto));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // toString() 결과에 기반한 예상 문자열 생성
        String expectedLog1 = String.format("%s | userId=%s, action=%s, description=%s",
                time1.format(formatter), user1.getId(), "login", "Login success");
        String expectedLog2 = String.format("%s | userId=%s, action=%s, description=%s",
                time2.format(formatter), user1.getId(), "update", "Profile updated");
        String expectedLog3 = String.format("%s | userId=%s, action=%s, description=%s",
                time1.format(formatter), user2.getId(), "login", "Login success");

        // 사용자 1 검증
        LogListResponseDto dto1 = dtoMap.get(user1.getId());
        assertNotNull(dto1);
        assertEquals("Alice", dto1.getName());
        List<String> user1Logs = dto1.getLog();
        assertEquals(2, user1Logs.size());
        assertTrue(user1Logs.contains(expectedLog1));
        assertTrue(user1Logs.contains(expectedLog2));

        // 사용자 2 검증
        LogListResponseDto dto2 = dtoMap.get(user2.getId());
        assertNotNull(dto2);
        assertEquals("Bob", dto2.getName());
        List<String> user2Logs = dto2.getLog();
        assertEquals(1, user2Logs.size());
        assertTrue(user2Logs.contains(expectedLog3));
    }
}