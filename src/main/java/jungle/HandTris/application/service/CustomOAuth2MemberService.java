package jungle.HandTris.application.service;

import jungle.HandTris.application.impl.CustomNicknameServiceImpl;
import jungle.HandTris.domain.Member;
import jungle.HandTris.domain.MemberRecord;
import jungle.HandTris.domain.repo.MemberRecordRepository;
import jungle.HandTris.domain.repo.MemberRepository;
import jungle.HandTris.presentation.dto.response.CustomOAuth2Member;
import jungle.HandTris.presentation.dto.response.OAuth2MemberDetailRes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomOAuth2MemberService extends DefaultOAuth2UserService  {

    private final MemberRepository memberRepository;
    private final MemberRecordRepository memberRecordRepository;
    private final BCryptPasswordService bCryptPasswordService;
    private final CustomNicknameServiceImpl customNicknameService;

    @Value("${spring.security.oauth2.client.temppassword}")
    String tempPassword;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 유저 정보 가져오기
        Map<String, Object> oAuth2MemberAttributes = super.loadUser(userRequest).getAttributes();

        // resistrationId 가져오기 (third-party id)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // userNameAttributeName 가져오기
        String userNameAttributeName =
                userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // 유저 정보 dto 생성
        OAuth2MemberDetailRes oAuth2MemberDetailRes = OAuth2MemberDetailRes.of(registrationId, oAuth2MemberAttributes);

        // 회원가입 및 로그인
        Member member = getOrSave(oAuth2MemberDetailRes);

        // OAuth2User로 반환
        return new CustomOAuth2Member(member,oAuth2MemberAttributes, userNameAttributeName);
    }

    private Member getOrSave(OAuth2MemberDetailRes oAuth2MemberDetailRes) {
        return Optional.ofNullable(memberRepository.findByUsername(oAuth2MemberDetailRes.getUsername()))
                .orElseGet(() -> {
                    String encodedPassword = bCryptPasswordService.encode(tempPassword); // 임시 비밀번호 암호화
                    String uniqueNickname = customNicknameService.generateUniqueNickname();

                    Member newMember = oAuth2MemberDetailRes.toEntity(encodedPassword, uniqueNickname);

                    MemberRecord newMemberRecord = new MemberRecord(newMember); // MemberRecord 생성

                    memberRepository.save(newMember); // 회원 정보 저장
                    memberRecordRepository.save(newMemberRecord); // 전적 정보 저장

                    return newMember;
                });
    }
}
