package project.jaeryang.bank.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
// 클라이언트에서 자격 증명(쿠키,인증헤더) 요청 허용 설정하면 반드시 특정 Origin을 명시하거나 allowedOriginPatterns()를 사용해야 한다.
//                .allowedOrigins("*")
                .allowedOriginPatterns("http://localhost:*")
                ;
    }
}
