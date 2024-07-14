package jungle.HandTris.presentation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jungle.HandTris.application.service.MemberProfileService;
import jungle.HandTris.application.service.MemberRecordService;
import jungle.HandTris.domain.MemberRecord;
import jungle.HandTris.global.dto.ResponseEnvelope;
import jungle.HandTris.global.validation.UserNicknameFromJwt;
import jungle.HandTris.presentation.dto.request.MemberUpdateReq;
import jungle.HandTris.presentation.dto.response.MemberProfileRes;
import jungle.HandTris.presentation.dto.response.MemberProfileUpdateDetailsRes;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberProfileController {
    private final MemberProfileService memberProfileService;
    private final MemberRecordService memberRecordService;

    @GetMapping("/mypage")
    public ResponseEnvelope<MemberProfileRes> myPage(@UserNicknameFromJwt String nickname) {
        MemberRecord memberRecord = memberRecordService.getMemberRecord(nickname);
        MemberProfileRes memberProfileRes = new MemberProfileRes(memberRecord);
        return ResponseEnvelope.of(memberProfileRes);
    }

    @PatchMapping("/mypage")
    public ResponseEnvelope<MemberProfileUpdateDetailsRes> updateInfo(
            HttpServletRequest request,
            @Valid MemberUpdateReq memberUpdateReq,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "deleteProfileImage", required = false, defaultValue = "false") boolean deleteProfileImage
    ) {

        MemberProfileUpdateDetailsRes updateMemberDetails = memberProfileService.updateMemberProfile(request, memberUpdateReq, profileImage, deleteProfileImage);

        return ResponseEnvelope.of(updateMemberDetails);
    }
}