package jungle.HandTris.presentation;

import jakarta.validation.Valid;
import jungle.HandTris.application.service.GameRoomService;
import jungle.HandTris.domain.GameRoom;
import jungle.HandTris.global.dto.ResponseEnvelope;
import jungle.HandTris.global.validation.UserNicknameFromJwt;
import jungle.HandTris.presentation.dto.request.GameRoomDetailReq;
import jungle.HandTris.presentation.dto.response.GameRoomDetailRes;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
public class GameRoomController {
    private final GameRoomService gameRoomService;


    @GetMapping
    public ResponseEnvelope<List<GameRoomDetailRes>> getGameRoomList() {
        List<GameRoomDetailRes> gameRoomDetailResList = gameRoomService.getGameRoomList().stream().map(GameRoomDetailRes::new).toList();
        ResponseEnvelope<List<GameRoomDetailRes>> result = ResponseEnvelope.of(gameRoomDetailResList);
        return result;
    }

    @PostMapping
    public ResponseEnvelope<UUID> createGameRoom(@UserNicknameFromJwt String nickname, @Valid @RequestBody GameRoomDetailReq gameRoomDetailReq) {
        UUID gameUuid = gameRoomService.createGameRoom(nickname, gameRoomDetailReq);
        ResponseEnvelope<UUID> result = ResponseEnvelope.of(gameUuid);
        return result;
    }

    @PostMapping("/{roomCode}/enter")
    public ResponseEnvelope<GameRoomDetailRes> enterGameRoom(@UserNicknameFromJwt String nickname, @PathVariable("roomCode") String roomCode) {
        GameRoom gameRoom = gameRoomService.enterGameRoom(nickname, roomCode);
        GameRoomDetailRes gameRoomDetailRes = new GameRoomDetailRes(gameRoom);
        ResponseEnvelope<GameRoomDetailRes> result = ResponseEnvelope.of(gameRoomDetailRes);
        return result;
    }

    @PostMapping("/{roomCode}/exit")
    public ResponseEnvelope<String> exitGameRoom(@UserNicknameFromJwt String nickname, @PathVariable("roomCode") String roomCode) {
        GameRoom gameRoom = gameRoomService.exitGameRoom(nickname, roomCode);
        ResponseEnvelope<String> result = ResponseEnvelope.of(roomCode);
        return result;
    }

}