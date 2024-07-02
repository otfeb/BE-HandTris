package jungle.HandTris.application.service;

import jungle.HandTris.domain.GameMember;
import jungle.HandTris.domain.GameRoom;
import jungle.HandTris.presentation.dto.request.GameRoomDetailReq;

import java.util.List;
import java.util.UUID;

public interface GameRoomService {
    List<GameRoom> getGameRoomList();

    GameRoom getGameRoom(String roomCode);

    GameMember getGameMember(String roomCode);

    UUID createGameRoom(GameRoomDetailReq gameRoomDetailReq);

    GameRoom enterGameRoom(String roomCode);

    GameRoom exitGameRoom(String roomCode);

}
