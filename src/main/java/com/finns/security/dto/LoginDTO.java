package com.finns.security.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginDTO {
    private int user_no;
    private String username;
    private String password;
    private Date birth;
    private String mbti_name;
    private String img_url;

    public static LoginDTO of(HttpServletRequest request) throws AuthenticationException {
        ObjectMapper om = new ObjectMapper();
        try {
            return om.readValue(request.getInputStream(), LoginDTO.class); // JSON == 객체
        } catch (Exception e) {
            throw new BadCredentialsException("username 또는 password가 없습니다.");
        }
    }

}
