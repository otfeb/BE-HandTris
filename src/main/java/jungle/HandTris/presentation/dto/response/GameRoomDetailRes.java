package jungle.HandTris.presentation.dto.response;

import jungle.HandTris.domain.GameRoom;
import jungle.HandTris.domain.GameStatus;

import java.util.UUID;

public record GameRoomDetailRes(
        long id,
        long participantCount,
        long participantLimit,
        UUID uuid,
        GameStatus gameStatus
) {
    public GameRoomDetailRes(GameRoom gameRoom) {
        this(gameRoom.getId(),
                gameRoom.getParticipantCount(),
                gameRoom.getParticipantLimit(),
                gameRoom.getRoomCode(),
                gameRoom.getGameStatus());
    }
}
