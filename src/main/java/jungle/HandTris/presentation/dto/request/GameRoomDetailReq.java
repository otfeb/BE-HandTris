package jungle.HandTris.presentation.dto.request;

import jakarta.validation.constraints.Min;

public record GameRoomDetailReq(
        @Min(2)
        long participantLimit
) {
}
