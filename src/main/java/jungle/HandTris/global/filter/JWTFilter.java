package jungle.HandTris.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jungle.HandTris.domain.Member;
import jungle.HandTris.domain.exception.AccessTokenExpiredException;
import jungle.HandTris.domain.exception.InvalidTokenFormatException;
import jungle.HandTris.global.jwt.JWTUtil;
import jungle.HandTris.presentation.dto.response.CustomMemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization= request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7);

        //토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {
            throw new AccessTokenExpiredException();
        }

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String subject = jwtUtil.getSubject(token);

        if (!subject.equals("AccessToken")) {
            throw new InvalidTokenFormatException();
        }

        //토큰에서 nickname 획득
        String nickname = jwtUtil.getNickname(token);

        //userEntity를 생성하여 값 set
        Member memberData = new Member("user", nickname, "temppassword");

        //UserDetails에 회원 정보 객체 담기
        CustomMemberDetails customUserDetails = new CustomMemberDetails(memberData);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
