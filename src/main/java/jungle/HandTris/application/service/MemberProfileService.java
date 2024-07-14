package jungle.HandTris.application.service;

import jungle.HandTris.presentation.dto.request.MemberUpdateReq;
import jungle.HandTris.presentation.dto.response.MemberRecordDetailRes;
import jungle.HandTris.presentation.dto.response.ReissueTokenRes;
import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;

public interface MemberProfileService {
    Pair<String, MemberRecordDetailRes> getMemberProfileWithStatsByNickname(String nickname);
    ReissueTokenRes changeMemberNickname(String nickname, MemberUpdateReq memberUpdateReq);
    void changeMemberProfileImage(String nickname, MultipartFile profileImage);
    void deleteMemberProfileImage(String nickname);
}
