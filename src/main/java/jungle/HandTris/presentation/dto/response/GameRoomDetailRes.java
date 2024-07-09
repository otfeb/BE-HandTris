package jungle.HandTris.presentation.dto.response;

import jungle.HandTris.domain.GameRoom;

import java.util.UUID;

public record GameRoomDetailRes(
        long id,
        String title,
        String creator,
        long participantCount,
        long participantLimit,
        UUID roomCode
) {
    public GameRoomDetailRes(GameRoom gameRoom) {
        this(gameRoom.getId(),
                gameRoom.getTitle(),
                gameRoom.getCreator(),
                gameRoom.getParticipantCount(),
                gameRoom.getParticipantLimit(),
                gameRoom.getRoomCode());
    }
}
