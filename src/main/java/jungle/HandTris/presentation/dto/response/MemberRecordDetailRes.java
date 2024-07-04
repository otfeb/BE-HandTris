package jungle.HandTris.presentation.dto.response;

import jungle.HandTris.domain.MemberRecord;

import java.math.BigDecimal;

public record MemberRecordDetailRes(
        long win,
        long lose,
        BigDecimal rate
) {
    public MemberRecordDetailRes(MemberRecord memberRecord) {
        this(memberRecord.getWin(),
                memberRecord.getLose(),
                memberRecord.getRate()
        );
    }

    @Override
    public String toString() {
        return "{" +
                "\"win\":" + win + "," +
                "\"lose\":" + lose + "," +
                "\"rate\":" + rate + "," +
                '}';
    }
}
