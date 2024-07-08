package jungle.HandTris.global.config.security;

import jungle.HandTris.application.service.CustomOAuth2MemberService;
import jungle.HandTris.global.filter.JWTFilter;
import jungle.HandTris.global.handler.JWTAccessDeniedHandler;
import jungle.HandTris.global.handler.JWTAuthenticateDeniedHandler;
import jungle.HandTris.global.handler.OAuth2FailureHandler;
import jungle.HandTris.global.handler.OAuth2SuccessHandler;
import jungle.HandTris.global.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final JWTAccessDeniedHandler jwtAccessDeniedHandler;
    private final JWTAuthenticateDeniedHandler jwtAuthenticateDeniedHandler;
    private final CustomOAuth2MemberService customOAuth2MemberService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf((auth) -> auth.disable()
                )
                .formLogin((auth) -> auth.disable()
                )
                .httpBasic((auth) -> auth.disable()
                )
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/auth/signin", "/auth/signup","/reissue", "/oauth2/loginSuccess").permitAll()
                        .anyRequest().authenticated()
                )
                .cors(Customizer.withDefaults()
                )
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                            .userService(customOAuth2MemberService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(exceptionHandle -> {
            exceptionHandle.accessDeniedHandler(jwtAccessDeniedHandler);
            exceptionHandle.authenticationEntryPoint(jwtAuthenticateDeniedHandler);
        });

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://handtris.vercel.app"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("content-type", "authorization", "x-requested-with"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
        configuration.setExposedHeaders(Collections.singletonList("Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
