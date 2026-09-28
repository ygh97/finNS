package com.finns.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.finns.member.dto.MemberDTO;
import com.finns.member.dto.MemberJoinDTO;
import com.finns.member.dto.MemberUpdateDTO;
import com.finns.member.exception.PasswordMissmatchException;
import com.finns.member.mapper.MemberMapper;
import com.finns.security.account.domain.AuthVO;
import com.finns.security.account.domain.MemberVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private static final String DEFAULT_AVATAR = "/assets/media/avatars/blank.png";

    final PasswordEncoder passwordEncoder;
    final MemberMapper mapper;

    @Override
    public boolean checkDuplicate(String username) {
        MemberVO member = mapper.checkUsername(username);
        return member != null;
    }

    @Override
    public MemberDTO get(String username) {
        MemberVO member = Optional.ofNullable(mapper.get(username))
                .orElseThrow(() -> new NoSuchElementException("회원 정보를 찾을 수 없습니다."));
        return MemberDTO.of(member);
    }

    // 아바타 저장 후 img_url 경로 반환, 아바타가 없으면 null
    private String saveAvatar(MultipartFile avatar) {
        // 아바타 업로드
        if (avatar != null && !avatar.isEmpty()) {
            // 파일 확장자 가져오기
            String originalFilename = avatar.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase(); // 확장자 추출
            }

            // 지원하는 확장자 확인 (png 또는 jpg만 허용)
            if (!extension.equals(".png") && !extension.equals(".jpg") && !extension.equals(".jpeg")) {
                log.error("지원하지 않는 파일 형식입니다: {}", extension);
                throw new RuntimeException("지원하지 않는 파일 형식입니다. PNG 또는 JPG 파일만 업로드할 수 있습니다.");
            }

            // 파일 저장 경로 설정
            File uploadDir = new File("c:/upload/avatar");

            // 디렉토리가 존재하지 않으면 생성
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    log.error("아바타 파일 저장 경로를 생성할 수 없습니다.");
                    throw new RuntimeException("아바타 파일 저장 경로를 생성할 수 없습니다.");
                }
            }

            // 저장할 파일 경로 설정 - 사용자 입력이 파일명에 들어가지 않도록 UUID 사용 (확장자 유지)
            String fileName = UUID.randomUUID() + extension;
            File dest = new File(uploadDir, fileName);
            try {
                avatar.transferTo(dest);  // 파일 저장
                return "/upload/avatar/" + fileName;  // img_url 경로 반환
            } catch (IOException e) {
                log.error("아바타 파일 저장 중 오류 발생: {}", e.getMessage());
                throw new RuntimeException("아바타 파일 저장 중 오류가 발생했습니다.", e);
            }
        }
        return null; // 아바타가 없을 경우 null 반환
    }




    @Transactional
    @Override
    public MemberDTO join(MemberJoinDTO dto) {
        MemberVO member = dto.toVO();
        member.setPassword(passwordEncoder.encode(member.getPassword())); // 비밀번호 암호화
        String imgUrl = saveAvatar(dto.getAvatar());
        member.setImg_url(imgUrl != null ? imgUrl : DEFAULT_AVATAR); // 화면이 img_url을 그대로 쓰므로 비워두지 않음
        mapper.insert(member);
        AuthVO authority = new AuthVO();
        authority.setUsername(member.getUsername());
        authority.setAuthority("ROLE_MEMBER");
        mapper.insertAuth(authority);
        return get(member.getUsername());
    }

    @Transactional
    @Override
    public MemberDTO update(MemberUpdateDTO member) {
        // 기존 회원 정보 가져오기
        MemberVO vo = Optional.ofNullable(mapper.get(member.getUsername()))
                .orElseThrow(() -> new NoSuchElementException("회원 정보를 찾을 수 없습니다."));

        // 비밀번호를 바꾸려면 현재 비밀번호가 반드시 맞아야 한다
        String encodedPassword = vo.getPassword(); // 기존 비밀번호 유지
        if (member.getNewPassword() != null && !member.getNewPassword().isEmpty()) {
            if (member.getOldPassword() == null
                    || !passwordEncoder.matches(member.getOldPassword(), vo.getPassword())) {
                throw new PasswordMissmatchException("현재 비밀번호가 일치하지 않습니다.");
            }
            encodedPassword = passwordEncoder.encode(member.getNewPassword());
        }

        // 새 아바타가 있을 때만 img_url 변경
        mapper.update(member.toVO(encodedPassword, saveAvatar(member.getAvatar())));

        return get(member.getUsername());
    }


}