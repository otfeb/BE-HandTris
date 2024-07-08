package jungle.HandTris.application.impl;

import jungle.HandTris.application.service.TetrisService;
import jungle.HandTris.domain.repo.GameRoomRepository;
import jungle.HandTris.presentation.dto.response.RoomOwnerRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TetrisServiceImpl implements TetrisService {

    private final GameRoomRepository gameRoomRepository;

    public RoomOwnerRes checkRoomOwnerAndReady(String roomCode) {
        int participant = gameRoomRepository.findByRoomCode(UUID.fromString(roomCode)).orElseThrow().getParticipantCount();
        System.out.println("참가자 수 : " + participant);
        if (participant == 1) {
            return new RoomOwnerRes(true);
        }
        return new RoomOwnerRes(false);
    }
}
