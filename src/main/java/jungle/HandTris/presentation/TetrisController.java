package jungle.HandTris.presentation;

import jungle.HandTris.application.service.GameRoomService;
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
    private final GameRoomService gameRoomService;

    @MessageMapping("/{roomCode}/tetris")
    public void handleTetrisMessage(@DestinationVariable("roomCode") String roomCode, TetrisMessageReq message, SimpMessageHeaderAccessor headerAccessor) {
        String otherUser = headerAccessor.getHeader("otherUser").toString();

        // 세션 ID를 이용하여 메시지 전송
        messagingTemplate.convertAndSendToUser(otherUser, "queue/tetris/" + roomCode, message);
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

    @MessageMapping("/{roomCode}/disconnect")
    public void handleDisconnect(SimpMessageHeaderAccessor headerAccessor, @DestinationVariable(value = "roomCode") String roomCode) {
        System.out.println("\n========================================= controller disconnect send =========================================");
        System.out.println(headerAccessor);

        // 1. 혼자 있는 상황에서 탈주

        // 2. 둘이 있는 상황에서 탈주
        // 2-1. 게임 중일 때 탈주
        // 2-2. 게임 대기일 때 탈주

        // playing 중인 게임에서 탈주한 경우
        if (gameRoomService.isGameRoomPlaying(roomCode)) {
            String[][] emptyBoard = {{}};
            // 탈주 유저의 상대에게 승리 메세지 보내기
            TetrisMessageReq win = new TetrisMessageReq(emptyBoard, true, false);
            String otherUser = headerAccessor.getHeader("otherUser").toString();
            messagingTemplate.convertAndSendToUser(otherUser, "queue/tetris/" + roomCode, win);
        }


        // 탈주 유저의 상대에게 승리 메세지 보내기
        String[][] emptyBoard = {{}};
        TetrisMessageReq message = new TetrisMessageReq(emptyBoard, true, false);
        String otherUser = headerAccessor.getHeader("otherUser").toString();
        messagingTemplate.convertAndSendToUser(otherUser, "queue/tetris/" + roomCode, message);


        String user = headerAccessor.getHeader("User").toString();

        System.out.println("방장 최신화");
        // 방장 최신화
        RoomOwnerRes roomOwnerRes = tetrisService.checkRoomOwnerAndReady(roomCode);
        messagingTemplate.convertAndSend("/topic/owner/" + roomCode, roomOwnerRes);

        System.out.println("DB 최신화");
        // DB 최신화
        gameRoomService.exitGameRoom(user, roomCode);
        System.out.println("\n========================================= disconnect send 종료 =========================================");
    }

}