package jungle.HandTris.presentation;

import jungle.HandTris.application.service.TetrisService;
import jungle.HandTris.presentation.dto.request.RoomStateReq;
import jungle.HandTris.presentation.dto.request.TetrisMessageReq;
import jungle.HandTris.presentation.dto.response.RoomOwnerRes;
import jungle.HandTris.presentation.dto.response.RoomStateRes;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TetrisController {
    private final SimpMessagingTemplate messagingTemplate;
    private final TetrisService tetrisService;

    @MessageMapping("/{roomCode}/tetris")
    public void handleTetrisMessage(@DestinationVariable("roomCode") String roomCode, TetrisMessageReq message, SimpMessageHeaderAccessor headerAccessor) {
        String otherUser = headerAccessor.getHeader("otherUser").toString();

        // 세션 ID를 이용하여 메시지 전송
        messagingTemplate.convertAndSendToUser(otherUser, "queue/tetris/" + roomCode, message);
    }

    @MessageMapping("/{roomCode}/owner/info")
    public void roomOwnerInfo(@DestinationVariable("roomCode") String roomCode) {
        RoomOwnerRes roomOwnerRes = tetrisService.checkRoomOwnerAndReady(roomCode);
        System.out.println("Controller OWer info");
        messagingTemplate.convertAndSend("/topic/owner/" + roomCode, roomOwnerRes);
        System.out.println("Controller OWer info After");
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