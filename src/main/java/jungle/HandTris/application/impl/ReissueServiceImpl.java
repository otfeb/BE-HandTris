package jungle.HandTris.application.impl;

import jakarta.servlet.http.HttpServletRequest;
import jungle.HandTris.application.service.ReissueService;
import jungle.HandTris.domain.exception.InvalidTokenFormatException;
import jungle.HandTris.domain.exception.RefreshTokenExpiredException;
import jungle.HandTris.global.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReissueServiceImpl implements ReissueService {

    private final JWTUtil jwtUtil;

    public String reissue (HttpServletRequest request) {
        String refreshToken = jwtUtil.resolveRefreshToken(request);

        //토큰 소멸 시간 검증
        if (jwtUtil.isExpired(refreshToken)) {
            throw new RefreshTokenExpiredException();
        }

        String subject = jwtUtil.getSubject(refreshToken);

        if(!subject.equals("RefreshToken")) {
            throw new InvalidTokenFormatException();
        }

        String nickname = jwtUtil.getNickname(refreshToken);
        String newAccessToken = jwtUtil.createAccessToken(nickname);

        return newAccessToken;
    }
}
