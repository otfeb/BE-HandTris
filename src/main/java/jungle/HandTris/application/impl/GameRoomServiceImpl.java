package jungle.HandTris.application.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jungle.HandTris.application.service.GameRoomService;
import jungle.HandTris.application.service.MemberProfileService;
import jungle.HandTris.domain.GameMember;
import jungle.HandTris.domain.GameRoom;
import jungle.HandTris.domain.exception.GameRoomNotFoundException;
import jungle.HandTris.domain.exception.MemberNotFoundException;
import jungle.HandTris.domain.exception.ParticipantLimitedException;
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

    private static final String GAME_MEMBER_KEY_PREFIX = "gameMember:";

    @Override
    public List<GameRoom> getGameRoomList() {
        return gameRoomRepository.findAllByGameStatusNotPlaying();
    }

    @Override
    public UUID createGameRoom(@UserNicknameFromJwt String nickname, GameRoomDetailReq gameRoomDetailReq) {
        GameRoom createdGameRoom = new GameRoom(nickname, gameRoomDetailReq.title());
        gameRoomRepository.save(createdGameRoom);

        String roomCode = createdGameRoom.getRoomCode().toString();
        String gameKey = GAME_MEMBER_KEY_PREFIX + roomCode;
        GameMember gameMember = new GameMember(roomCode);
        Pair<String, MemberRecordDetailRes> memberDetails = memberProfileService.getMemberProfileWithStatsByNickname(nickname);
        gameMember.addMember(new GameMemberEssentialDTO(nickname, memberDetails.getFirst(), memberDetails.getSecond()));
        redisTemplate.opsForValue().set(gameKey, gameMember.toString());
        return createdGameRoom.getRoomCode();
    }

    @Override
    public GameRoom enterGameRoom(@UserNicknameFromJwt String nickname, String roomCode) {
        GameRoom gameRoom = gameRoomRepository.findByRoomCode(UUID.fromString(roomCode)).orElseThrow(GameRoomNotFoundException::new);

        Pair<String, MemberRecordDetailRes> memberDetails = memberProfileService.getMemberProfileWithStatsByNickname(nickname);

        if (gameRoom.getParticipantCount() == gameRoom.getParticipantLimit()) {
            throw new ParticipantLimitedException();
        }

        gameRoom.enter();

        // Redis에 매핑 정보 업데이트
        String gameKey = GAME_MEMBER_KEY_PREFIX + roomCode;
        String gameMemberGen = redisTemplate.opsForValue().get(gameKey);
        GameMember gameMember = generateGameMember(gameMemberGen);
        gameMember.addMember(new GameMemberEssentialDTO(nickname, memberDetails.getFirst(), memberDetails.getSecond()));
        redisTemplate.opsForValue().set(GAME_MEMBER_KEY_PREFIX + roomCode, gameMember.toString());

        return gameRoom;
    }

    @Override
    public GameRoom exitGameRoom(@UserNicknameFromJwt String nickname, String roomCode) {
        GameRoom gameRoom = gameRoomRepository.findByRoomCode(UUID.fromString(roomCode))
                .orElseThrow(GameRoomNotFoundException::new);

        gameRoom.exit();

        String key = GAME_MEMBER_KEY_PREFIX + roomCode;
        String gameMemberGen = redisTemplate.opsForValue().get(key);

        if (gameMemberGen == null) {
            throw new GameRoomNotFoundException();
        }

        GameMember gameMember = generateGameMember(gameMemberGen);

        // nickname으로 프로필 Url과 전적 추출
        Pair<String, MemberRecordDetailRes> memberDetails = memberProfileService.getMemberProfileWithStatsByNickname(nickname);
        GameMemberEssentialDTO dto = new GameMemberEssentialDTO(nickname, memberDetails.getFirst(), memberDetails.getSecond());

        if (!gameMember.isPresentMember(nickname)) {
            throw new MemberNotFoundException();
        }

        // gameMember에서 해당 유저 삭제
        gameMember.removeMember(nickname);

        // 만약 인원이 0명이라면
        if (gameRoom.getParticipantCount() == 0) {
            gameRoomRepository.delete(gameRoom);
            redisTemplate.delete(key);
        }
        // 인원이 남아있다면
        else {
            redisTemplate.delete(key);
            redisTemplate.opsForValue().set(key, gameMember.toString());
        }
        return gameRoom;
    }

    private GameMember generateGameMember(String gameMemberGen) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (gameMemberGen != null) {
                return objectMapper.readValue(gameMemberGen, GameMember.class);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        throw new MemberNotFoundException();
    }
}
