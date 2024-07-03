package jungle.HandTris.presentation.dto.response;

import jungle.HandTris.domain.MemberRecord;

import java.math.BigDecimal;

public record MemberRecordDetailRes(
        long win,
        long lose,
        BigDecimal rate,
        String avgTime
) {
    public MemberRecordDetailRes(MemberRecord memberRecord) {
        this(memberRecord.getWin(),
                memberRecord.getLose(),
                memberRecord.getRate(),
                memberRecord.getAvgTime().toString());
    }

    @Override
    public String toString() {
        return "{" +
                "\"win\":" + win + "," +
                "\"lose\":" + lose + "," +
                "\"rate\":" + rate + "," +
                "\"avgTime\":\"" + avgTime + "\"" +
                '}';
    }
}
