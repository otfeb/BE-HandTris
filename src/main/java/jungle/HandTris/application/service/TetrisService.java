package jungle.HandTris.application.service;

import jungle.HandTris.presentation.dto.request.TetrisMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TetrisService {
    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastTetrisMessage(TetrisMessageRequest message) {
        messagingTemplate.convertAndSend("/topic/tetris", message);
    }
}