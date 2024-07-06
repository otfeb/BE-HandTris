package jungle.HandTris.global.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jungle.HandTris.domain.Member;
import jungle.HandTris.global.jwt.JWTUtil;
import jungle.HandTris.presentation.dto.response.CustomOAuth2Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2Member customMemberDetails = (CustomOAuth2Member) authentication.getPrincipal();
        Member member = customMemberDetails.member();
        String nickname = member.getNickname();

        String accessToken = jwtUtil.createAccessToken(nickname);
        System.out.println("accessToken = " + accessToken);

        response.addCookie(createCookie("Authorization", accessToken));
        response.sendRedirect("https://handtris.vercel.app/");
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");
        cookie.setDomain("checkmatejungle.shop");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}
