package jungle.HandTris.global.config;

import jungle.HandTris.global.validation.UserNameFromJwtResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final UserNameFromJwtResolver userNameFromJwtResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userNameFromJwtResolver);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000", "https://handtris.vercel.app", "https://handtris-jungle.vercel.app")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        .allowedHeaders("content-type", "Authorization", "Authorization-Refresh", "x-requested-with")
                        .exposedHeaders("Set-Cookie")
//                        .exposedHeaders("content-type", "x-requested-with")
                        .allowCredentials(true);
            }
        };
    }

}
