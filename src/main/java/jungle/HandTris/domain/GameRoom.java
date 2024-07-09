package jungle.HandTris.domain;

import jakarta.persistence.*;
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

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String creator;

    private int participantCount;

    private int participantLimit;

    @Column(nullable = false)
    private UUID roomCode;

    public GameRoom(String creator, String title) {
        this.title = title;
        this.creator = creator;
        this.participantCount = 1;
        this.participantLimit = 2;
        this.roomCode = UUID.randomUUID();
    }

    public void enter() {
        this.participantCount++;
    }

    public void exit() {
        this.participantCount--;
    }

}
