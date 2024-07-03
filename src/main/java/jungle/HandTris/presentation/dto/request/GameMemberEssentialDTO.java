package jungle.HandTris.presentation.dto.request;

import jungle.HandTris.presentation.dto.response.MemberRecordDetailRes;

public record GameMemberEssentialDTO(String nickname, String profileImageUrl, MemberRecordDetailRes recordDetail) {
    @Override
    public String toString() {
        return "{" +
                "\"nickname\":\"" + nickname + "\"," +
                "\"profileImageUrl\":\"" + profileImageUrl + "\"," +
                "\"recordDetail\":" + recordDetail +
                '}';
    }
}
