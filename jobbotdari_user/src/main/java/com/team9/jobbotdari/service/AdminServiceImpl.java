package com.team9.jobbotdari.service;

import com.team9.jobbotdari.dto.response.UserListResponseDto;
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

    private static final ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<UserListResponseDto> getUserList() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserListResponseDto.class))
                .collect(Collectors.toList());
    }
}
