package jungle.HandTris.presentation;

import jungle.HandTris.application.service.GameRoomService;
import jungle.HandTris.application.service.TetrisService;
import jungle.HandTris.domain.GameMember;
import jungle.HandTris.presentation.dto.request.RoomStateReq;
import jungle.HandTris.presentation.dto.request.TetrisMessageReq;
import jungle.HandTris.presentation.dto.response.RoomOwnerRes;
import jungle.HandTris.presentation.dto.response.RoomStateRes;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
public class TetrisController {
    private final SimpMessagingTemplate messagingTemplate;
    private final GameRoomService gameRoomService;
    private final TetrisService tetrisService;

    @MessageMapping("/{roomCode}/tetris")
    public void handleTetrisMessage(@DestinationVariable String roomCode, TetrisMessageReq message) {
        GameMember gameMember = gameRoomService.getGameMember(roomCode); // GameRoomService에서 특정 방 가져오기

        if (gameMember != null) {
            Set<String> connectedUsers = gameMember.getMembers().stream()
                    .filter(memberId -> !memberId.equals(message.sender())) // 메시지 보낸 사람 제외
                    .collect(Collectors.toSet());

            connectedUsers.forEach(memberId ->
                    messagingTemplate.convertAndSendToUser(memberId, "queue/tetris", message)
            );
        }
    }

    @MessageMapping("/{roomCode}/owner/info")
    public void roomOwnerInfo(@DestinationVariable String roomCode) {
        RoomOwnerRes roomOwnerRes = tetrisService.checkRoomOwnerAndReady(roomCode);
        messagingTemplate.convertAndSend("/topic/owner/" + roomCode, roomOwnerRes); //TOPIC은 Prefix이기 때문에 roomCode가 뒤에 가도록 변경해주세요
    }

    @MessageMapping("/{roomCode}/tetris/ready")
    public void TetrisReady(@DestinationVariable String roomCode, RoomStateReq req) {
        if (req.isReady()) {
            RoomStateRes res = new RoomStateRes(true, false);
            messagingTemplate.convertAndSend("/topic/state/" + roomCode, res);
        }
    }

    @MessageMapping("/{roomCode}/tetris/start")
    public void TetrisStart(@DestinationVariable String roomCode, RoomStateReq req) {
        if (req.isStart()) {
            RoomStateRes res = new RoomStateRes(true, true);
            messagingTemplate.convertAndSend("/topic/state/" + roomCode, res);
        }
    }
}