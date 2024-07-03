package jungle.HandTris.application.impl;

import jakarta.transaction.Transactional;
import jungle.HandTris.application.service.GameRoomService;
import jungle.HandTris.domain.GameRoom;
import jungle.HandTris.domain.GameStatus;
import jungle.HandTris.domain.exception.GameRoomNotFoundException;
import jungle.HandTris.domain.exception.ParticipantLimitedException;
import jungle.HandTris.domain.exception.PlayingGameException;
import jungle.HandTris.domain.repo.GameRoomRepository;
import jungle.HandTris.presentation.dto.request.GameRoomDetailReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@Transactional
@RequiredArgsConstructor
public class GameRoomServiceImpl implements GameRoomService {
    private final GameRoomRepository gameRoomRepository;

    @Override
    public List<GameRoom> getGameRoomList() {
        return gameRoomRepository.findAllByGameStatusNotPlaying();
    }

    @Override
    public UUID createGameRoom(GameRoomDetailReq gameRoomDetailReq, String nickName) {
        GameRoom createdGameRoom = new GameRoom(gameRoomDetailReq.title(), nickName);
        gameRoomRepository.save(createdGameRoom);
        return createdGameRoom.getRoomCode();
    }

    @Override
    public GameRoom enterGameRoom(String roomCode) {
        GameRoom gameRoom = gameRoomRepository.findByRoomCode(UUID.fromString(roomCode)).orElseThrow(GameRoomNotFoundException::new);

        if (gameRoom.getGameStatus() == GameStatus.PLAYING) {
            throw new PlayingGameException();
        }

        if (gameRoom.getParticipantCount() == gameRoom.getParticipantLimit()) {
            throw new ParticipantLimitedException();
        }

        gameRoom.enter();
        return gameRoom;
    }

    @Override
    public GameRoom exitGameRoom(String roomCode) {
        GameRoom gameRoom = gameRoomRepository.findByRoomCode(UUID.fromString(roomCode)).orElseThrow(GameRoomNotFoundException::new);

        if (gameRoom.getGameStatus() == GameStatus.PLAYING) {
            throw new PlayingGameException();
        }
        gameRoom.exit();

        if (gameRoom.getParticipantCount() == 0) {
            gameRoomRepository.delete(gameRoom);
            return gameRoom;
        }
        return gameRoom;
    }
}
