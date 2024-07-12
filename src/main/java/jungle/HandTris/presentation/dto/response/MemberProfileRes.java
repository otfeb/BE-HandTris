package jungle.HandTris.presentation.dto.response;

import jungle.HandTris.domain.MemberRecord;

import java.math.BigDecimal;

public record MemberProfileRes(
        String nickname,
        String profileImageUrl,
        long win,
        long lose,
        BigDecimal winRate
) {
    public MemberProfileRes(MemberRecord memberRecord) {
        this(
                memberRecord.getMember().getNickname(),
                memberRecord.getMember().getProfileImageUrl(),
                memberRecord.getWin(),
                memberRecord.getLose(),
                memberRecord.getRate()
        );
    }

}