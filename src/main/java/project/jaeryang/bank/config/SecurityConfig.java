package project.jaeryang.bank.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import project.jaeryang.bank.domain.user.UserEnum;
import project.jaeryang.bank.util.CustomResponseUtil;

import java.util.Collections;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .headers(auth -> auth
                        .frameOptions(frameOptions -> frameOptions.disable()));
        http
                .csrf(auth -> auth.disable());
        http
                .cors(auth -> auth.configurationSource(corsConfigurationSource()));
        http
                .sessionManagement(auth -> auth
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)); //JSessionId를 서버쪽에서 관리x
        http
                .formLogin(auth -> auth.disable());
        http
                .httpBasic(auth -> auth.disable());

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/s/**").authenticated()
                        .requestMatchers("/api/admin/**").hasRole(UserEnum.ADMIN.name())
                        .anyRequest().permitAll()
                );
        // Exception 가로채기 (로그인을 안한 경우 authenticationEntryPoint 정의, 권한 문제면 accessDeniedHandler)
        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, accessDeniedException) -> {
                    CustomResponseUtil.unAuthenticated(response, "로그인을 진행해주세요");
                }));

        return http.build();
    }

    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*")); //모든 IP 주소 허용 (원래는 프론트엔드 IP만 허용)
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowCredentials(true); //클라이언트에서 쿠키 요청 허용
        configuration.setMaxAge(3600L);
        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.debug("디버그: BCryptPasswordEncoder 빈 등록됨");
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
