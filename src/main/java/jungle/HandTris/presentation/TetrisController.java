package jungle.HandTris.presentation;

import jungle.HandTris.application.service.GameRoomService;
import jungle.HandTris.application.service.TetrisService;
import jungle.HandTris.domain.GameRoom;
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
    private final GameRoomService gameRoomService;

    @MessageMapping("/{roomCode}/tetris")
    public void handleTetrisMessage(@DestinationVariable("roomCode") String roomCode, TetrisMessageReq message, SimpMessageHeaderAccessor headerAccessor) {
        String otherUser = headerAccessor.getHeader("otherUser").toString();

        // 세션 ID를 이용하여 메시지 전송
        messagingTemplate.convertAndSendToUser(otherUser, "queue/tetris/" + roomCode, message);
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

    @MessageMapping("/{roomCode}/owner/info")
    public void roomOwnerInfo(@DestinationVariable("roomCode") String roomCode, SimpMessageHeaderAccessor headerAccessor) {
        RoomOwnerRes roomOwnerRes = tetrisService.checkRoomOwnerAndReady(roomCode);
        messagingTemplate.convertAndSend("/topic/owner/" + roomCode, roomOwnerRes);
    }

    @MessageMapping("/{roomCode}/disconnect")
    public void handleDisconnect(SimpMessageHeaderAccessor headerAccessor, @DestinationVariable(value = "roomCode") String roomCode) {
        System.out.println("\n========================================= controller disconnect send =========================================");

        // playing 중인 게임에서 탈주한 경우
        // message에서 isStart 확인
        boolean isStart = headerAccessor.getFirstNativeHeader("isStart").equals("true");
        if (isStart) {
            String[][] emptyBoard = {{"#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030"},
                    {"#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030"},
                    {"#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030"},
                    {"#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030"},
                    {"#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030"},
                    {"#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030"},
                    {"#303030", "#303030", "#303030", "#303030", "orange", "#303030", "#303030", "#303030", "#303030", "#303030"},
                    {"#303030", "#303030", "#303030", "#303030", "orange", "#303030", "#303030", "#303030", "#303030", "#303030"},
                    {"#303030", "#303030", "#303030", "orange", "orange", "#303030", "#303030", "#303030", "#303030", "#303030"},
                    {"#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030"},
                    {"#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030"},
                    {"#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030"},
                    {"#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030"},
                    {"#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030"},
                    {"#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030"},
                    {"#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030"},
                    {"#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030"},
                    {"#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030"},
                    {"#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030"},
                    {"#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030", "#303030"}};
            // 탈주 유저의 상대에게 승리 메세지 보내기 ------------------------------
            TetrisMessageReq win = new TetrisMessageReq(emptyBoard, true, false, false);
            String otherUser = headerAccessor.getHeader("otherUser").toString();
            messagingTemplate.convertAndSendToUser(otherUser, "queue/tetris/" + roomCode, win);
        }

        // DB 최신화
        System.out.println("DB 최신화");
        String user = headerAccessor.getHeader("User").toString();
        GameRoom gameRoom = gameRoomService.exitGameRoom(user, roomCode);

        // 참여자가 나갔지만 누군가 남아 있다면
        if (gameRoom.getParticipantCount() != 0) {
            // 방장 최신화 : DB 최신화 아래 있어야 한다.
            System.out.println("방장 최신화");
            RoomOwnerRes roomOwnerRes = tetrisService.checkRoomOwnerAndReady(roomCode);
            messagingTemplate.convertAndSend("/topic/owner/" + roomCode, roomOwnerRes);
        }

        System.out.println("\n========================================= disconnect send 종료 =========================================");
    }

}