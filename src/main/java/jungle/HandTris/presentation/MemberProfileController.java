package jungle.HandTris.presentation;

import jakarta.validation.Valid;
import jungle.HandTris.application.service.MemberProfileService;
import jungle.HandTris.global.dto.ResponseEnvelope;
import jungle.HandTris.global.validation.UserNicknameFromJwt;
import jungle.HandTris.presentation.dto.request.MemberUpdateReq;
import jungle.HandTris.presentation.dto.response.ReissueTokenRes;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/change")
public class MemberProfileController {
    private final MemberProfileService memberProfileService;

    @PutMapping("/profileNickname") // 닉네임 변경
    public ResponseEnvelope<ReissueTokenRes> changeNickname (@UserNicknameFromJwt String nickname, @Valid @RequestBody MemberUpdateReq memberUpdateReq) {

        ReissueTokenRes token = memberProfileService.changeMemberNickname(nickname, memberUpdateReq);

        return ResponseEnvelope.of(token);
    }

    @PatchMapping("/profileImage") // 프로필 이미지 변경
    public ResponseEnvelope<String> changeProfileImage (@UserNicknameFromJwt String nickname, @RequestPart(value = "profileImage") MultipartFile profileImage) {

        memberProfileService.changeMemberProfileImage(nickname, profileImage);

        return ResponseEnvelope.of("Profile Image Change Successful");
    }

    @DeleteMapping("/profileImage") // 프로필 이미지 제거
    public ResponseEnvelope<String> deleteProfileImage (@UserNicknameFromJwt String nickname) {

        memberProfileService.deleteMemberProfileImage(nickname);

        return ResponseEnvelope.of("Profile Image Delete Successful");
    }

}
