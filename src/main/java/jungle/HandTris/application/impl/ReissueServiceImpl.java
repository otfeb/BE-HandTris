package jungle.HandTris.application.impl;

import jakarta.servlet.http.HttpServletRequest;
import jungle.HandTris.application.service.ReissueService;
import jungle.HandTris.domain.Member;
import jungle.HandTris.domain.exception.InvalidTokenFormatException;
import jungle.HandTris.domain.exception.RefreshTokenExpiredException;
import jungle.HandTris.domain.exception.UnauthorizedAccessException;
import jungle.HandTris.domain.repo.MemberRepository;
import jungle.HandTris.global.jwt.JWTUtil;
import jungle.HandTris.presentation.dto.response.ReissueTokenRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReissueServiceImpl implements ReissueService {

    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;

    public ReissueTokenRes reissue (HttpServletRequest request, String requestUsername) {
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
        Member member = memberRepository.findByUsername(requestUsername);

        if(!member.getRefreshToken().equals(refreshToken)) {
            throw new UnauthorizedAccessException();
        }

        String newAccessToken = jwtUtil.createAccessToken(nickname);
        String newRefreshToken = jwtUtil.createRefreshToken(nickname);

        member.updateRefreshToken(newRefreshToken);
        memberRepository.save(member);

        ReissueTokenRes token = new ReissueTokenRes(newAccessToken, newRefreshToken);

        return token;
    }
}
