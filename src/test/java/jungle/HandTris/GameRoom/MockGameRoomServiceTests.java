package jungle.HandTris.GameRoom;

import jakarta.validation.Valid;
import jungle.HandTris.application.impl.GameRoomServiceImpl;
import jungle.HandTris.domain.GameRoom;
import jungle.HandTris.domain.repo.GameRoomRepository;
import jungle.HandTris.presentation.dto.request.GameRoomDetailReq;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

// junit5에서 Mockito를 사용하기 위해 붙이는 어노테이션, Service 영역에 대한 단위 테스트를 위해서 사용
@ExtendWith(MockitoExtension.class)
public class MockGameRoomServiceTests {

    // 테스트를 위한 가짜 객체 생성
    @Mock
    private GameRoomRepository gameRoomRepository;

    // 생성시 Mock으로 만든 가짜 객체들을 주입받는 객체
    @InjectMocks
    private GameRoomServiceImpl gameServiceImpl;

    @Test
    @DisplayName("게임 목록 조회 Test")
    void getGameRoomListTest() {
        /* given : 테스트 사전 조건 설정 */
        // entity 생성을 위한 DTO도 실제 코드와 같이 @Valid로 입력값 검사
        GameRoomDetailReq gameRoomDetailReq1 = new @Valid GameRoomDetailReq("new game 1");
        GameRoom gameRoom1 = new GameRoom(gameRoomDetailReq1.title(), "nickname 1");
        GameRoomDetailReq gameRoomDetailReq2 = new @Valid GameRoomDetailReq("new game 2");
        GameRoom gameRoom2 = new GameRoom(gameRoomDetailReq2.title(), "nickname 2");
        List<GameRoom> expectedGameRoomList = Arrays.asList(gameRoom1, gameRoom2);

        // Repository 객체의 동작을 정의
        when(gameRoomRepository.findAllByGameStatusNotPlaying()).thenReturn(expectedGameRoomList);

        /* when */
        List<GameRoom> actualGameRoomList = gameServiceImpl.getGameRoomList();

        /* then */
        Assertions.assertThat(actualGameRoomList).isNotNull();
        Assertions.assertThat(actualGameRoomList.size()).isEqualTo(expectedGameRoomList.size());
        Assertions.assertThat(actualGameRoomList).containsExactlyInAnyOrderElementsOf(expectedGameRoomList);
    }

    @Test
    @DisplayName("게임 입장 Test")
    void enterGameRoomTest() {
        /* given : 테스트 사전 조건 설정 */
        GameRoomDetailReq gameRoomDetailReq = new @Valid GameRoomDetailReq("new game");
        GameRoom newgame = new GameRoom(gameRoomDetailReq.title(), "nickname");
        long beforeParticipantCount = newgame.getParticipantCount();
        String gameUuid = newgame.getRoomCode().toString();
        when(gameRoomRepository.findByRoomCode(UUID.fromString(gameUuid))).thenReturn(Optional.of(newgame));

        /* when : 실제 테스트 실행*/
        GameRoom enteredGameRoom = gameServiceImpl.enterGameRoom(gameUuid);

        /* then : 테스트 결과 검증*/
        Assertions.assertThat(enteredGameRoom).isNotNull();
        Assertions.assertThat(enteredGameRoom.getParticipantLimit()).isEqualTo(2);
        Assertions.assertThat(enteredGameRoom.getParticipantCount()).isEqualTo(beforeParticipantCount + 1);
    }


    @Test
    @DisplayName("플레이어의 게임 나가기 Test")
    void exitGameRoomByPlayerTest() {
        /* given : 테스트 사전 조건 설정 */
        GameRoomDetailReq gameRoomDetailReq = new @Valid GameRoomDetailReq("new game");
        GameRoom newgame = new GameRoom(gameRoomDetailReq.title(), "nickname");
        newgame.enter(); // 게임 임장
        gameRoomRepository.save(newgame);
        UUID roomCode = newgame.getRoomCode();
        when(gameRoomRepository.findByRoomCode(roomCode)).thenReturn(Optional.of(newgame));

        /* when : 실제 테스트 실행 */
        GameRoom exitedGameRoom = gameServiceImpl.exitGameRoom(newgame.getRoomCode().toString());

        /* then : 테스트 결과 검증 */
        Assertions.assertThat(exitedGameRoom).isNotNull();
        Assertions.assertThat(exitedGameRoom.getParticipantLimit()).isEqualTo(2);
        Assertions.assertThat(exitedGameRoom.getParticipantCount()).isEqualTo(1);
    }


}