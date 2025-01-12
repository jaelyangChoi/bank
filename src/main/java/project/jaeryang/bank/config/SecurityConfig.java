package project.jaeryang.bank.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import project.jaeryang.bank.config.jwt.JwtAuthenticationFilter;
import project.jaeryang.bank.config.jwt.JwtAuthorizationFilter;
import project.jaeryang.bank.domain.user.UserEnum;
import project.jaeryang.bank.util.CustomResponseUtil;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

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

        // JWT 필터 등록
        http
                .addFilterAt(new JwtAuthenticationFilter(authenticationManager(authenticationConfiguration))
                        , UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(new JwtAuthorizationFilter(authenticationManager(authenticationConfiguration))
                        , BasicAuthenticationFilter.class);

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/s/**").authenticated()
                        .requestMatchers("/api/admin/**").hasRole(UserEnum.ADMIN.name())
                        .anyRequest().permitAll()
                );

        // Exception 가로채기 (로그인을 안한 경우 authenticationEntryPoint 정의, 권한 문제면 accessDeniedHandler)
        http
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, accessDeniedException) -> {
                            CustomResponseUtil.fail(response, "로그인을 진행해주세요", HttpStatus.UNAUTHORIZED);
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            CustomResponseUtil.fail(response, "관리자 권한이 없습니다", HttpStatus.FORBIDDEN);
                        })
                );

        return http.build();
    }

    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true); //클라이언트에서 자격 증명(쿠키,인증헤더) 요청 허용. 설정하면 반드시 특정 Origin을 명시하거나 allowedOriginPatterns()를 사용
        configuration.setAllowedOriginPatterns(Collections.singletonList("http://localhost:*")); //모든 IP 주소 허용 (원래는 프론트엔드 IP만 허용)
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setMaxAge(3600L);
        configuration.setExposedHeaders(Collections.singletonList("Authorization")); //브라우저가 JS에 해당 응답 헤더를 노출해줌

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
