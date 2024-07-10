package jungle.HandTris.presentation.dto.response;

import jungle.HandTris.domain.MemberRecord;

import java.math.BigDecimal;

public record ParticipantRes(
        String nickname,
        String profileImageUrl,
        long win,
        long lose,
        BigDecimal winRate
) {
    public ParticipantRes(MemberRecord memberRecord) {
        this(
                memberRecord.getMember().getNickname(),
                memberRecord.getMember().getProfileImageUrl(),
                memberRecord.getWin(),
                memberRecord.getLose(),
                memberRecord.getRate()
        );
    }

}