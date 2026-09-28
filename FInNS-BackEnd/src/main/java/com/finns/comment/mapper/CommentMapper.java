package com.finns.comment.mapper;

import com.finns.comment.dto.CommentDTO;

import java.util.List;

public interface CommentMapper {
    // 소비내역 번호에 맞는 댓글 리스트 조회하는 메서드
    List<CommentDTO> getCommentByUser(Long postNo);

    // 새로운 댓글 저장
    void saveComment(CommentDTO commentDTO);

}
