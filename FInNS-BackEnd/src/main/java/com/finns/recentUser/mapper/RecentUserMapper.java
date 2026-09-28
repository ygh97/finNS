package com.finns.recentUser.mapper;

import com.finns.recentUser.dto.InsertRecentUserDTO;
import com.finns.recentUser.dto.RecentUserResponseDTO;

import java.util.List;

public interface RecentUserMapper {
    List<RecentUserResponseDTO> selectRecentUser(Long userNo);
    void insertRecentUser(InsertRecentUserDTO insertRecentUserDTO);
}
