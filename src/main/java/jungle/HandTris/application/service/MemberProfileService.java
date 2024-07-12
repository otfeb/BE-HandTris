package jungle.HandTris.application.service;

import jakarta.servlet.http.HttpServletRequest;
import jungle.HandTris.presentation.dto.request.MemberUpdateReq;
import jungle.HandTris.presentation.dto.response.MemberProfileUpdateDetailsRes;
import jungle.HandTris.presentation.dto.response.MemberRecordDetailRes;
import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;

public interface MemberProfileService {
    Pair<String, MemberRecordDetailRes> getMemberProfileWithStatsByNickname(String nickname);

    MemberProfileUpdateDetailsRes updateMemberProfile(HttpServletRequest request, MemberUpdateReq memberUpdateReq, MultipartFile profileImage, Boolean deleteProfileImage, String username);
}
