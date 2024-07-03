package jungle.HandTris.domain;

import jakarta.persistence.*;
import jungle.HandTris.presentation.dto.request.GameRoomDetailReq;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long participantCount;

    private long participantLimit;

    @Column(nullable = false)
    private UUID roomCode;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private GameStatus gameStatus;

    public GameRoom(GameRoomDetailReq gameRoomDetailReq) {
        this.participantCount = 1;
        this.participantLimit = 2;
        this.roomCode = UUID.randomUUID();
        this.gameStatus = GameStatus.NON_PLAYING;
    }

    public void enter() {
        this.participantCount++;
    }

    public void exit() {
        this.participantCount--;
    }

}
