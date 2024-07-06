package jungle.HandTris.presentation.dto.response;

import jungle.HandTris.domain.Member;
import jungle.HandTris.domain.exception.InvalidSocialLoginExcepion;
import lombok.Builder;

import java.util.Map;

@Builder
public class OAuth2MemberDetailRes {
    private String username;
    private String profileImageUrl;

    public String getUsername() {
        return this.username;
    }

    public static OAuth2MemberDetailRes of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "kakao" -> ofKakao(attributes);
            case "naver" -> ofNaver(attributes);
            default -> throw new InvalidSocialLoginExcepion();
        };
    }

    public static OAuth2MemberDetailRes ofKakao(Map<String, Object> attributes) {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        return OAuth2MemberDetailRes.builder()
                .username("kakao_" + attributes.get("id").toString())
                .profileImageUrl(properties.get("profile_image").toString())
                .build();
    }

    public static OAuth2MemberDetailRes ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2MemberDetailRes.builder()
                .username("naver" + response.get("id").toString())
                .profileImageUrl(response.get("profile_image").toString())
                .build();
    }

    public Member toEntity(String password, String nickname) {
        return new Member(username, password, nickname, profileImageUrl);
    }

}
