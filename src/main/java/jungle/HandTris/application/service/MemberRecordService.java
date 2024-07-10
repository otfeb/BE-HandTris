package jungle.HandTris.application.service;

import jungle.HandTris.domain.MemberRecord;
import jungle.HandTris.presentation.dto.request.GameResultReq;

import java.util.List;

public interface MemberRecordService {
    MemberRecord getMemberRecord(String nickname);

    MemberRecord updateMemberRecord(GameResultReq gameResultReq, String nickname);

    List<MemberRecord> getParticipants(String roomCode, String nickname);
}
