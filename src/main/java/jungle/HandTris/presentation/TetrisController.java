package jungle.HandTris.presentation;

import jungle.HandTris.application.service.TetrisService;
import jungle.HandTris.presentation.dto.request.RoomStateReq;
import jungle.HandTris.presentation.dto.request.TetrisMessageReq;
import jungle.HandTris.presentation.dto.response.RoomOwnerRes;
import jungle.HandTris.presentation.dto.response.RoomStateRes;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TetrisController {
    private final SimpMessagingTemplate messagingTemplate;
    private final TetrisService tetrisService;

    @MessageMapping("/{roomCode}/tetris")
    public void handleTetrisMessage(@DestinationVariable("roomCode") String roomCode, TetrisMessageReq message, @Header("otherUser") String otherUser) {
        // Sender가 아닌 유저에게 메시지 전달
        messagingTemplate.convertAndSendToUser(otherUser, "queue/tetris", message);
    }

    @MessageMapping("/{roomCode}/owner/info")
    public void roomOwnerInfo(@DestinationVariable("roomCode") String roomCode) {
        RoomOwnerRes roomOwnerRes = tetrisService.checkRoomOwnerAndReady(roomCode);
        messagingTemplate.convertAndSend("/topic/owner/" + roomCode, roomOwnerRes);
    }

    @MessageMapping("/{roomCode}/tetris/ready")
    public void TetrisReady(@DestinationVariable("roomCode") String roomCode, RoomStateReq req) {
        if (req.isReady()) {
            RoomStateRes res = new RoomStateRes(true, false);
            messagingTemplate.convertAndSend("/topic/state/" + roomCode, res);
        }
    }

    @MessageMapping("/{roomCode}/tetris/start")
    public void TetrisStart(@DestinationVariable("roomCode") String roomCode, RoomStateReq req) {
        if (req.isStart()) {
            RoomStateRes res = new RoomStateRes(true, true);
            messagingTemplate.convertAndSend("/topic/state/" + roomCode, res);
        }
    }
}