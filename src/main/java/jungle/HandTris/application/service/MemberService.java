package jungle.HandTris.application.service;

import jakarta.servlet.http.HttpServletResponse;
import jungle.HandTris.domain.Member;
import jungle.HandTris.domain.exception.DuplicateNicknameException;
import jungle.HandTris.domain.exception.DuplicateUsernameException;
import jungle.HandTris.domain.exception.PasswordMismatchException;
import jungle.HandTris.domain.exception.UserNotFoundException;
import jungle.HandTris.domain.repo.MemberRepository;
import jungle.HandTris.global.jwt.JWTUtil;
import jungle.HandTris.presentation.dto.request.MemberRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;

    @Transactional
    public Long signin (MemberRequest memberRequest, HttpServletResponse response) {
        String username = memberRequest.username();
        String password = memberRequest.password();

        Member member = Optional.ofNullable(memberRepository.findByUsername(username))
                .orElseThrow(() -> new UserNotFoundException());

        // 비밀번호 확인
        if (!bCryptPasswordEncoder.matches(password, member.getPassword())) {
            throw new PasswordMismatchException();
        }
        response.addHeader(jwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(member.getUsername(), member.getNickname()));

        return member.getId();
    }

    @Transactional
    public void signup(MemberRequest memberRequest) {
        boolean usernameExists = memberRepository.existsByUsername(memberRequest.username());
        boolean nicknameExists = memberRepository.existsByNickname(memberRequest.nickname());

        if (nicknameExists) {
            throw new DuplicateNicknameException();
        }

        if (usernameExists) {
            throw new DuplicateUsernameException();
        }

        String username = memberRequest.username();
        String password = memberRequest.password();
        String nickname = memberRequest.nickname();

        Member data = new Member(username, bCryptPasswordEncoder.encode(password), nickname);

        memberRepository.save(data);
    }
}
