package jungle.HandTris.application.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jungle.HandTris.application.service.GameRoomService;
import jungle.HandTris.application.service.MemberProfileService;
import jungle.HandTris.domain.GameMember;
import jungle.HandTris.domain.GameRoom;
import jungle.HandTris.domain.GameStatus;
import jungle.HandTris.domain.exception.GameRoomNotFoundException;
import jungle.HandTris.domain.exception.ParticipantLimitedException;
import jungle.HandTris.domain.exception.PlayingGameException;
import jungle.HandTris.domain.repo.GameMemberRepository;
import jungle.HandTris.domain.repo.GameRoomRepository;
import jungle.HandTris.global.validation.UserNicknameFromJwt;
import jungle.HandTris.presentation.dto.request.GameMemberEssentialDTO;
import jungle.HandTris.presentation.dto.request.GameRoomDetailReq;
import jungle.HandTris.presentation.dto.response.MemberRecordDetailRes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@Transactional
@RequiredArgsConstructor
public class GameRoomServiceImpl implements GameRoomService {
    private final GameRoomRepository gameRoomRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final MemberProfileService memberProfileService;
    private final GameMemberRepository gameMemberRepository;

    private static final String GAME_MEMBER_KEY_PREFIX = "gameMember:";

    @Override
    public List<GameRoom> getGameRoomList() {
        return gameRoomRepository.findAllByGameStatusNotPlaying();
    }

    @Override
    public GameMember getGameRoom(String roomCode) {
        String gameRoomKey = GAME_MEMBER_KEY_PREFIX + roomCode;
        String gameMemberGen = redisTemplate.opsForValue().get(gameRoomKey);

        GameMember gameMember = generateGameMember(gameMemberGen);

        return gameMember;
    }

    @Override
    public UUID createGameRoom(@UserNicknameFromJwt String nickname, GameRoomDetailReq gameRoomDetailReq) {
        GameRoom createdGameRoom = new GameRoom(gameRoomDetailReq);
        gameRoomRepository.save(createdGameRoom);
        // nickname으로 프로필 Url과 전적 추출
        Pair<String, MemberRecordDetailRes> memberDetails = memberProfileService.getMemberProfileWithStatsByNickname(nickname);
        // Redis에 매핑 정보 저장
        String roomCode = createdGameRoom.getRoomCode().toString();
        GameMember gameMember = new GameMember(roomCode);
        gameMember.addMember(new GameMemberEssentialDTO(nickname, memberDetails.getFirst(), memberDetails.getSecond()));
        String gameKey = GAME_MEMBER_KEY_PREFIX + roomCode;
        redisTemplate.opsForValue().set(gameKey, gameMember.toString());

        return createdGameRoom.getRoomCode();
    }

    @Override
    public GameRoom enterGameRoom(@UserNicknameFromJwt String nickname, String roomCode) {
        GameRoom gameRoom = gameRoomRepository.findByRoomCode(UUID.fromString(roomCode)).orElseThrow(GameRoomNotFoundException::new);

        if (gameRoom.getGameStatus() == GameStatus.PLAYING) {
            throw new PlayingGameException();
        }

        // nickname으로 프로필 Url과 전적 추출
        Pair<String, MemberRecordDetailRes> memberDetails = memberProfileService.getMemberProfileWithStatsByNickname(nickname);

        if (gameRoom.getParticipantCount() == gameRoom.getParticipantLimit()) {
            throw new ParticipantLimitedException();
        }

        gameRoom.enter();

        // Redis에 매핑 정보 업데이트
        System.out.println(GAME_MEMBER_KEY_PREFIX + roomCode);
        String key = GAME_MEMBER_KEY_PREFIX + roomCode;

        String gameMemberGen = redisTemplate.opsForValue().get(key);

        GameMember gameMember = generateGameMember(gameMemberGen);
        System.out.println(gameMember);
        gameMember.addMember(new GameMemberEssentialDTO(nickname, memberDetails.getFirst(), memberDetails.getSecond()));
        redisTemplate.opsForValue().set(GAME_MEMBER_KEY_PREFIX + roomCode, gameMember.toString());

        return gameRoom;
    }

    @Override
    public GameRoom exitGameRoom(@UserNicknameFromJwt String nickname, String roomCode) {
        GameRoom gameRoom = gameRoomRepository.findByRoomCode(UUID.fromString(roomCode)).orElseThrow(GameRoomNotFoundException::new);

        if (gameRoom.getGameStatus() == GameStatus.PLAYING) {
            throw new PlayingGameException();
        }
        gameRoom.exit();

        // nickname으로 프로필 Url과 전적 추출
        Pair<String, MemberRecordDetailRes> memberDetails = memberProfileService.getMemberProfileWithStatsByNickname(nickname);

        // Redis에서 매핑 정보 업데이트
        String gameMemberGen = redisTemplate.opsForValue().get(GAME_MEMBER_KEY_PREFIX + roomCode);
        GameMember gameMember = generateGameMember(gameMemberGen);
        gameMember.addMember(new GameMemberEssentialDTO(nickname, memberDetails.getFirst(), memberDetails.getSecond()));

        if (gameRoom.getParticipantCount() == 0) {
            gameRoomRepository.delete(gameRoom);
            redisTemplate.delete(GAME_MEMBER_KEY_PREFIX + roomCode);
        } else {
            redisTemplate.delete(GAME_MEMBER_KEY_PREFIX + roomCode);
            redisTemplate.opsForValue().set(GAME_MEMBER_KEY_PREFIX + roomCode, gameMember.toString());
        }
        return gameRoom;
    }

    private GameMember generateGameMember(String gameMemberGen) {

        System.out.println("gameMemberGen:" + gameMemberGen);
        ObjectMapper objectMapper = new ObjectMapper();
        GameMember gameMember = null;
        try {
            if (gameMemberGen != null) {
                gameMember = objectMapper.readValue(gameMemberGen, GameMember.class);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // 예외 처리
        }
        System.out.println("ops:" + gameMember);

        if (gameMember == null) {
            throw new GameRoomNotFoundException();
        }

        return gameMember;
    }
}
