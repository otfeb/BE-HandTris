package jungle.HandTris.presentation;

import jakarta.validation.Valid;
import jungle.HandTris.application.service.MemberRecordService;
import jungle.HandTris.domain.MemberRecord;
import jungle.HandTris.global.dto.ResponseEnvelope;
import jungle.HandTris.global.validation.UserNicknameFromJwt;
import jungle.HandTris.presentation.dto.request.GameResultReq;
import jungle.HandTris.presentation.dto.response.MemberProfileRes;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/records")
public class MemberRecordController {

    private final MemberRecordService memberRecordService;

    @GetMapping
    public ResponseEnvelope<MemberProfileRes> getMyRecord(@UserNicknameFromJwt String nickname) {
        MemberRecord memberRecord = memberRecordService.getMemberRecord(nickname);
        MemberProfileRes memberProfileRes = new MemberProfileRes(memberRecord);
        return ResponseEnvelope.of(memberProfileRes);
    }

    @PutMapping
    public ResponseEnvelope<MemberProfileRes> updateMemberRecord(@Valid @RequestBody GameResultReq gameResultReq, @UserNicknameFromJwt String nickname) {
        MemberRecord memberRecord = memberRecordService.updateMemberRecord(gameResultReq, nickname);
        MemberProfileRes memberProfileRes = new MemberProfileRes(memberRecord);
        return ResponseEnvelope.of(memberProfileRes);
    }

    @GetMapping("/{nickname}")
    public ResponseEnvelope<MemberProfileRes> getMemberRecord(@PathVariable("nickname") String nickname) {
        MemberRecord memberRecord = memberRecordService.getMemberRecord(nickname);
        MemberProfileRes memberProfileRes = new MemberProfileRes(memberRecord);
        return ResponseEnvelope.of(memberProfileRes);
    }

    @GetMapping("/participant/{roomCode}")
    public ResponseEnvelope<List<MemberProfileRes>> getParticipants(@PathVariable("roomCode") String roomCode, @UserNicknameFromJwt String nickname) {
        List<MemberRecord> participantsMemberRecord = memberRecordService.getParticipants(roomCode, nickname);
        List<MemberProfileRes> memberProfileResList = new ArrayList<>();
        for (MemberRecord memberRecord : participantsMemberRecord) {
            if (memberRecord == null)
                continue;
            MemberProfileRes memberProfileRes = new MemberProfileRes(memberRecord);
            memberProfileResList.add(memberProfileRes);
        }
        return ResponseEnvelope.of(memberProfileResList);
    }
}
