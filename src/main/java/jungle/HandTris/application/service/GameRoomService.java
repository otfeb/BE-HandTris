package jungle.HandTris.application.service;

import jungle.HandTris.domain.GameMember;
import jungle.HandTris.domain.GameRoom;
import jungle.HandTris.global.validation.UserNicknameFromJwt;
import jungle.HandTris.presentation.dto.request.GameRoomDetailReq;

import java.util.List;
import java.util.UUID;

public interface GameRoomService {
    List<GameRoom> getGameRoomList();

    GameMember getGameRoom(String roomCode);

    UUID createGameRoom(@UserNicknameFromJwt String nickname, GameRoomDetailReq gameRoomDetailReq);

    GameRoom enterGameRoom(@UserNicknameFromJwt String nickname, String roomCode);

    GameRoom exitGameRoom(@UserNicknameFromJwt String nickname, String roomCode);

    boolean isGameRoomPlaying(String roomCode);
}
