package com.team9.jobbotdari.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LogListResponseDto {
    private Long userId;
    private String name;
    private List<String> log;
}
