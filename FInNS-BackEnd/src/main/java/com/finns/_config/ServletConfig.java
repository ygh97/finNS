package com.finns._config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@EnableWebMvc
@ComponentScan(basePackages = {"com.finns.**"})
public class ServletConfig  implements WebMvcConfigurer {

    // 업로드 파일 저장 폴더 - application.properties의 upload.dir, 없으면 사용자 홈 아래
    public static final String UPLOAD_DIR = "${upload.dir:${user.home}/finns-upload}";

    @Value(UPLOAD_DIR)
    private String uploadDir;

    // 화면 경로는 모두 SPA(index.html)로 보냄 - 새로고침·직접 접속 대응
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        for (String path : SpaRoutes.PATHS) {
            registry.addViewController(path).setViewName(SpaRoutes.INDEX);
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("/resources/assets/");
        // 업로드된 아바타 이미지 (MemberServiceImpl.saveAvatar 저장 위치)
        String uploadLocation = Paths.get(uploadDir).toUri().toString();
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(uploadLocation.endsWith("/") ? uploadLocation : uploadLocation + "/");
    }


    //	Servlet 3.0 파일 업로드 사용시
    @Bean
    public MultipartResolver multipartResolver() {
        StandardServletMultipartResolver resolver = new StandardServletMultipartResolver();
        return resolver;
    }

}
