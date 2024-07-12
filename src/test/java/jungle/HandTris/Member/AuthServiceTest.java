package jungle.HandTris.Member;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jungle.HandTris.application.service.AuthService;
import jungle.HandTris.application.service.MemberProfileService;
import jungle.HandTris.application.service.MemberRecordService;
import jungle.HandTris.domain.Member;
import jungle.HandTris.domain.MemberRecord;
import jungle.HandTris.domain.exception.*;
import jungle.HandTris.domain.repo.MemberRepository;
import jungle.HandTris.presentation.dto.request.MemberRequest;
import jungle.HandTris.presentation.dto.request.MemberUpdateReq;
import jungle.HandTris.presentation.dto.response.MemberProfileDetailsRes;
import jungle.HandTris.presentation.dto.response.MemberProfileUpdateDetailsRes;
import jungle.HandTris.presentation.dto.response.MemberRecordDetailRes;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Set;

@SpringBootTest
@Transactional
public class AuthServiceTest {

    @Autowired
    AuthService authService;
    @Autowired
    MemberProfileService memberProfileService;
    @Autowired
    MemberRecordService memberRecordService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    private Validator validator;

    @Nested
    @DisplayName("회원 가입")
    class Signup {

        @Test
        @DisplayName("정상 회원가입")
        public void testSignupSuccess() {
            // given
            MemberRequest member = new MemberRequest("user1", "1q2w3e4r!", "user1");

            // when
            authService.signup(member);

            // then
            Member findMember = memberRepository.findByUsername(member.username());

            Assertions.assertThat(findMember.getUsername()).isEqualTo(member.username());
            Assertions.assertThat(findMember.getNickname()).isEqualTo(member.nickname());

        }

        @Nested
        @DisplayName("Username 관련 예외 처리")
        class Username {

            @Test
            @DisplayName("중복 Username")
            public void testSignupFailWithDuplicateUsername() {
                // given
                MemberRequest member1 = new MemberRequest("user1", "1q2w3e4r!", "user1");
                MemberRequest member2 = new MemberRequest("user1", "1q2w3e4r!", "user2");
                authService.signup(member1);

                // when & then

                Assertions.assertThatThrownBy(() -> authService.signup(member2))
                        .isInstanceOf(DuplicateUsernameException.class);
            }

            @Test
            @DisplayName("3 자리 이하 Username")
            public void testSignupFailWithTooShortUsername() {
                // given
                MemberRequest member = new MemberRequest("use", "1q2w3e4r!", "user1");

                // when
                Set<ConstraintViolation<MemberRequest>> violations = validator.validate(member);

                // then
                Assertions.assertThat(violations).isNotEmpty();
                Assertions.assertThat(violations.stream().findFirst().get().getMessage()).isEqualTo("아이디는 최소 4자 이상, 10자 이하여야 합니다");
            }

            @Test
            @DisplayName("11 자리 이상 Username")
            public void testSignupFailWithTooLongUsername() {
                // given
                MemberRequest member = new MemberRequest("longlonglonguser", "1q2w3e4r!", "user1");

                // when
                Set<ConstraintViolation<MemberRequest>> violations = validator.validate(member);

                // then
                Assertions.assertThat(violations).isNotEmpty();
                Assertions.assertThat(violations.stream().findFirst().get().getMessage()).isEqualTo("아이디는 최소 4자 이상, 10자 이하여야 합니다");
            }

            @Test
            @DisplayName("알파벳 대문자가 포함된 Username")
            public void testSignupFailWithUppercaseUsername() {
                // given
                MemberRequest member = new MemberRequest("User1", "1q2w3e4r!", "user1");

                // when
                Set<ConstraintViolation<MemberRequest>> violations = validator.validate(member);

                // then
                Assertions.assertThat(violations).isNotEmpty();
                Assertions.assertThat(violations.stream().findFirst().get().getMessage()).isEqualTo("아이디는 알파벳 소문자(a~z), 숫자(0~9)로 구성되어야 합니다.");
            }

            @Test
            @DisplayName("특수 문자가 포함된 Username")
            public void testSignupFailWithSpecialCharacterUsername() {
                // given
                MemberRequest member = new MemberRequest("user1!", "1q2w3e4r!", "user1");

                // when
                Set<ConstraintViolation<MemberRequest>> violations = validator.validate(member);

                // then
                Assertions.assertThat(violations).isNotEmpty();
                Assertions.assertThat(violations.stream().findFirst().get().getMessage()).isEqualTo("아이디는 알파벳 소문자(a~z), 숫자(0~9)로 구성되어야 합니다.");
            }

            @Test
            @DisplayName("`admin` 작성된 Username")
            public void testSignupFailWithAdminUsername() {
                // given
                MemberRequest member = new MemberRequest("admin", "1q2w3e4r!", "user1");

                // when
                Set<ConstraintViolation<MemberRequest>> violations = validator.validate(member);

                // then
                Assertions.assertThat(violations).isNotEmpty();
                Assertions.assertThat(violations.stream().findFirst().get().getMessage()).isEqualTo("admin은 사용할 수 없습니다.");
            }

        }

        @Nested
        @DisplayName("Nickname 관련 예외 처리")
        class Nickname {

            @Test
            @DisplayName("중복 Nickname")
            public void testSignupFailWithDuplicateNickname() {
                // given
                MemberRequest member1 = new MemberRequest("user1", "1q2w3e4r!", "user1");
                MemberRequest member2 = new MemberRequest("user2", "1q2w3e4r!", "user1");
                authService.signup(member1);

                // when & then

                Assertions.assertThatThrownBy(() -> authService.signup(member2))
                        .isInstanceOf(DuplicateNicknameException.class);

            }

            @Test
            @DisplayName("3 자리 이하 Nickname")
            public void testSignupFailWithTooShortNickname() {
                // given
                MemberRequest member = new MemberRequest("user1", "1q2w3e4r!", "use");

                // when
                Set<ConstraintViolation<MemberRequest>> violations = validator.validate(member);

                // then
                Assertions.assertThat(violations).isNotEmpty();
                Assertions.assertThat(violations.stream().findFirst().get().getMessage()).isEqualTo("닉네임은 최소 4자 이상, 20자 이하여야 합니다");
            }

            @Test
            @DisplayName("21 자리 이상 Nickname")
            public void testSignupFailWithTooLongNickname() {
                // given
                MemberRequest member = new MemberRequest("user1", "1q2w3e4r!", "longlonglonglonglonguser");

                // when
                Set<ConstraintViolation<MemberRequest>> violations = validator.validate(member);

                // then
                Assertions.assertThat(violations).isNotEmpty();
                Assertions.assertThat(violations.stream().findFirst().get().getMessage()).isEqualTo("닉네임은 최소 4자 이상, 20자 이하여야 합니다");
            }

            @Test
            @DisplayName("특수 문자가 포함된 Nickname")
            public void testSignupFailWithSpecialCharacterNickname() {
                // given
                MemberRequest member = new MemberRequest("user1", "1q2w3e4r!", "user1^^");

                // when
                Set<ConstraintViolation<MemberRequest>> violations = validator.validate(member);

                // then
                Assertions.assertThat(violations).isNotEmpty();
                Assertions.assertThat(violations.stream().findFirst().get().getMessage()).isEqualTo("닉네임은 알파벳 대소문자와 숫자만 사용 가능합니다.");
            }

            @ParameterizedTest
            @ValueSource(strings = {"admin", "administator", "useradmin", "adminuser"})
            @DisplayName("admin 작성된 Nickname")
            public void testSignupFailWithAdminNickname(String nickname) {
                // given
                MemberRequest member = new MemberRequest("user1", "1q2w3e4r!", nickname);

                // when
                Set<ConstraintViolation<MemberRequest>> violations = validator.validate(member);

                // then
                Assertions.assertThat(violations).isNotEmpty();
                Assertions.assertThat(violations.stream().findFirst().get().getMessage()).isEqualTo("admin은 사용할 수 없습니다.");
            }

        }
    }

    @Nested
    @DisplayName("로그인")
    class Signin {

        @Test
        @DisplayName("정상 로그인")
        public void testLoginSuccess() {
            // given
            MemberRequest member = new MemberRequest("user1", "1q2w3e4r!", "user1");
            authService.signup(member);

            // when
            Pair<Member, String> result = authService.signin(member);

            // then
            Assertions.assertThat(result).isNotNull();

            Assertions.assertThat(result.getFirst()).isNotNull(); // Member 객체 확인
            Assertions.assertThat(result.getSecond()).isNotBlank(); // Access Token 확인

            Assertions.assertThat(result.getFirst().getUsername()).isEqualTo("user1");
            Assertions.assertThat(result.getFirst().getNickname()).isEqualTo("user1");

        }

        @Test
        @DisplayName("존재하지 않는 Username으로 접근")
        public void testLoginFailWithNonexistentUsername() {
            // given
            MemberRequest member = new MemberRequest("user1", "1q2w3e4r!", "user1");

            // when & then
            Assertions.assertThatThrownBy(() -> authService.signin(member))
                    .isInstanceOf(UserNotFoundException.class);

        }

        @Test
        @DisplayName("일치하지 않은 Password 입력")
        public void testLoginFailWithIncorrectPassword() {
            // given
            MemberRequest member = new MemberRequest("user1", "1q2w3e4r!", "user1");
            authService.signup(member);


            // when
            MemberRequest incorrectMember = new MemberRequest("user1", "wrongPassword", "user1");
            Assertions.assertThatThrownBy(() -> authService.signin(incorrectMember))
                    .isInstanceOf(PasswordMismatchException.class);
            // then

        }
    }

    @Nested
    @DisplayName("마이페이지")
    class MyPage {
        @Test
        @DisplayName("정상 마이페이지 호출")
        public void testMyPageSuccess() {
            // given
            MemberRequest requestMember = new MemberRequest("user1", "1q2w3e4r!", "user1");
            authService.signup(requestMember);
            Pair<Member, String> SigninResult = authService.signin(requestMember);
            Member member = SigninResult.getFirst();
            String token = SigninResult.getSecond();

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("Authorization", "Bearer " + token);

            MemberRecord memberRecord = memberRecordService.getMemberRecord(member.getNickname());
            MemberRecordDetailRes memberRecordDetailRes = new MemberRecordDetailRes(memberRecord);

            // when
            Pair<MemberProfileDetailsRes, MemberRecordDetailRes> profileResult =
                    memberProfileService.myPage(request, member.getUsername());

            // then
            Assertions.assertThat(profileResult.getFirst().nickname()).isEqualTo(member.getNickname());
            Assertions.assertThat(profileResult.getFirst().profileImageUrl()).isEqualTo(member.getProfileImageUrl());
            Assertions.assertThat(profileResult.getSecond()).isEqualTo(memberRecordDetailRes);
        }

        @Test
        @DisplayName("A유저가 B유저의 마이페이지 호출")
        public void testMyPageConnectWrongUser() {
            // given
            MemberRequest requestMember = new MemberRequest("user1", "1q2w3e4r!", "user1");
            authService.signup(requestMember);
            String token = authService.signin(requestMember).getSecond();

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("Authorization", "Bearer " + token);

            // when & then
            Assertions.assertThatThrownBy(() -> {
                Pair<MemberProfileDetailsRes, MemberRecordDetailRes> result = memberProfileService.myPage(request, "user2");
            }).isInstanceOf(UnauthorizedAccessException.class);
        }
    }

    @Nested
    @DisplayName("회원 정보 요청")
    class GetMemberProfile {

    }

    @Nested
    @DisplayName("회원 정보 수정")
    class ChangeMemberProfile {
        @Nested
        @DisplayName("닉네임 변경")
        class ChangeMemberNickname {
            @Test
            @DisplayName("정상 닉네임 변경")
            public void testChangeMemberNicknameSuccess() {
                // given
                MemberRequest requestMember = new MemberRequest("user1", "1q2w3e4r!", "user1");
                authService.signup(requestMember);
                Pair<Member, String> SigninResult = authService.signin(requestMember);
                Member member = SigninResult.getFirst();
                String token = SigninResult.getSecond();

                MockHttpServletRequest request = new MockHttpServletRequest();
                request.addHeader("Authorization", "Bearer " + token);

                MemberUpdateReq changeNickname = new MemberUpdateReq("user2");

                // when
                MemberProfileUpdateDetailsRes changeMemberProfile = memberProfileService.updateMemberProfile(
                        request, changeNickname, null, false, member.getUsername());

                // then
                Assertions.assertThat(changeMemberProfile.nickname()).isEqualTo(member.getNickname());
                Assertions.assertThat(changeMemberProfile.token()).isNotEqualTo(token);
            }

            @Test
            @DisplayName("이전과 동일한 닉네임일 때")
            public void testChangeMemberNicknameWithSameNickname() {
                // given
                MemberRequest requestMember = new MemberRequest("user1", "1q2w3e4r!", "user1");
                authService.signup(requestMember);
                Pair<Member, String> SigninResult = authService.signin(requestMember);
                Member member = SigninResult.getFirst();
                String token = SigninResult.getSecond();

                MockHttpServletRequest request = new MockHttpServletRequest();
                request.addHeader("Authorization", "Bearer " + token);

                MemberUpdateReq changeNickname = new MemberUpdateReq(member.getNickname());

                // when & then
                Assertions.assertThatThrownBy(() -> {
                    memberProfileService.updateMemberProfile(
                            request, changeNickname, null, null, member.getUsername());
                }).isInstanceOf(NicknameNotChangedException.class);
            }

            @Test
            @DisplayName("이미 존재하는 닉네임일 때")
            public void testChangeMemberNicknameWithDuplicateNickname() {
                // given
                MemberRequest requestMember1 = new MemberRequest("user1", "1q2w3e4r!", "user1");
                MemberRequest requestMember2 = new MemberRequest("user2", "1q2w3e4r!", "user2");
                authService.signup(requestMember1);
                authService.signup(requestMember2);
                Pair<Member, String> SigninResult = authService.signin(requestMember1);
                Member member = SigninResult.getFirst();
                String token = SigninResult.getSecond();

                MockHttpServletRequest request = new MockHttpServletRequest();
                request.addHeader("Authorization", "Bearer " + token);

                MemberUpdateReq changeNickname = new MemberUpdateReq(requestMember2.nickname());

                // when & then
                Assertions.assertThatThrownBy(() -> {
                    memberProfileService.updateMemberProfile(
                            request, changeNickname, null, null, member.getUsername());
                }).isInstanceOf(DuplicateNicknameException.class);
            }
        }

        @Nested
        @DisplayName("프로필 사진 변경")
        class ChangeMemberProfileImage {

            @Value("${cloud.aws.s3.defaultImage}")
            String defaultImage;

            @Test
            @DisplayName("정상 프로필 사진 변경")
            public void testChangeMemberProfileImageSuccess() {
                // given
                MemberRequest requestMember = new MemberRequest("user1", "1q2w3e4r!", "user1");
                authService.signup(requestMember);
                Pair<Member, String> SigninResult = authService.signin(requestMember);
                Member member = SigninResult.getFirst();
                String token = SigninResult.getSecond();

                MockHttpServletRequest request = new MockHttpServletRequest();
                request.addHeader("Authorization", "Bearer " + token);

                MemberUpdateReq changeNickname = new MemberUpdateReq(null);

                // 변경하려는 프로필 사진
                MockMultipartFile changeProfileImage = new MockMultipartFile(
                        "image", // 파라미터 이름
                        "image.png", // 파일 이름
                        "image/png", // 컨텐츠 타입
                        ("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQ" +
                                "UBAScY42YAAAAASUVORK5CYII=").getBytes() // PNG 이미지 데이터 (Base64)
                );

                // when
                MemberProfileUpdateDetailsRes changeMemberProfile = memberProfileService.updateMemberProfile(
                        request, changeNickname, changeProfileImage, false, member.getUsername());

                // then
                Assertions.assertThat(changeMemberProfile.profileImageUrl()).isEqualTo(member.getProfileImageUrl());
            }

            @Test
            @DisplayName("정상 프로필 사진 제거")
            public void testChangeMemberDefaultProfileImageSuccess() {
                // given
                MemberRequest requestMember = new MemberRequest("user1", "1q2w3e4r!", "user1");
                authService.signup(requestMember);
                Pair<Member, String> SigninResult = authService.signin(requestMember);
                Member member = SigninResult.getFirst();
                String token = SigninResult.getSecond();

                MockHttpServletRequest request = new MockHttpServletRequest();
                request.addHeader("Authorization", "Bearer " + token);

                MemberUpdateReq changeNickname = new MemberUpdateReq(null);
                // 변경하려는 프로필 사진
                MockMultipartFile changeProfileImage = new MockMultipartFile(
                        "image", // 파라미터 이름
                        "image.png", // 파일 이름
                        "image/png", // 컨텐츠 타입
                        "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=".getBytes() // PNG 이미지 데이터 (Base64)
                );

                MemberProfileUpdateDetailsRes changeMemberProfile = memberProfileService.updateMemberProfile(
                        request, changeNickname, changeProfileImage, false, member.getUsername());

                Assertions.assertThat(changeMemberProfile.profileImageUrl()).isEqualTo(member.getProfileImageUrl());

                // when
                memberProfileService.updateMemberProfile(
                        request, changeNickname, null, true, member.getUsername());
                // then
                Assertions.assertThat(defaultImage).isEqualTo(member.getProfileImageUrl());
            }

            @Test
            @DisplayName("이미지 파일이 아닐 때")
            public void testChangeMemberNicknameWithSameNickname() {
                // given
                MemberRequest requestMember = new MemberRequest("user1", "1q2w3e4r!", "user1");
                authService.signup(requestMember);
                Pair<Member, String> SigninResult = authService.signin(requestMember);
                Member member = SigninResult.getFirst();
                String token = SigninResult.getSecond();

                MockHttpServletRequest request = new MockHttpServletRequest();
                request.addHeader("Authorization", "Bearer " + token);

                MemberUpdateReq changeNickname = new MemberUpdateReq(null);
                // 변경하려는 프로필 사진
                MockMultipartFile changeProfileImage = new MockMultipartFile(
                        "image", // 파라미터 이름
                        "image.txt", // 파일 이름
                        "text/plain", // 컨텐츠 타입
                        "Hello, World!".getBytes() // 파일 내용
                );

                // when & then
                Assertions.assertThatThrownBy(() -> {
                    memberProfileService.updateMemberProfile(
                            request, changeNickname, changeProfileImage, false, member.getUsername());
                }).isInstanceOf(InvalidImageTypeException.class);
            }

            @Test
            @DisplayName("지정한 이미지 확장자 타입이 아닐 때 (png, jpg, jpeg만 허용)")
            public void testChangeMemberNicknameWithDuplicateNickname() {
                // given
                MemberRequest requestMember = new MemberRequest("user1", "1q2w3e4r!", "user1");
                authService.signup(requestMember);
                Pair<Member, String> SigninResult = authService.signin(requestMember);
                Member member = SigninResult.getFirst();
                String token = SigninResult.getSecond();

                MockHttpServletRequest request = new MockHttpServletRequest();
                request.addHeader("Authorization", "Bearer " + token);

                MemberUpdateReq changeNickname = new MemberUpdateReq(null);
                // 변경하려는 프로필 사진
                MockMultipartFile changeProfileImage = new MockMultipartFile(
                        "image", // 파라미터 이름
                        "image.gif", // 파일 이름
                        "image/gif", // 컨텐츠 타입
                        ("R0lGODlhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNw" +
                                "fjZ0frl3/zy7////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                                "AAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQ" +
                                "AosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLC" +
                                "VDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7").getBytes() // GIF 이미지 데이터 (Base64)
                );

                // when & then
                Assertions.assertThatThrownBy(() -> {
                    memberProfileService.updateMemberProfile(
                            request, changeNickname, changeProfileImage, false, member.getUsername());
                }).isInstanceOf(InvalidImageTypeException.class);
            }
        }
    }
}