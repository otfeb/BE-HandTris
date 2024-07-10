package jungle.HandTris.application.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jungle.HandTris.application.service.MemberRecordService;
import jungle.HandTris.domain.GameMember;
import jungle.HandTris.domain.Member;
import jungle.HandTris.domain.MemberRecord;
import jungle.HandTris.domain.exception.MemberNotFoundException;
import jungle.HandTris.domain.exception.MemberRecordNotFoundException;
import jungle.HandTris.domain.repo.MemberRecordRepository;
import jungle.HandTris.domain.repo.MemberRepository;
import jungle.HandTris.presentation.dto.request.GameMemberEssentialDTO;
import jungle.HandTris.presentation.dto.request.GameResult;
import jungle.HandTris.presentation.dto.request.GameResultReq;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberRecordServiceImpl implements MemberRecordService {

    private final MemberRecordRepository memberRecordRepository;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String GAME_MEMBER_KEY_PREFIX = "gameMember:";

    @Override
    public MemberRecord getMemberRecord(String nickname) {

        Optional<Member> member = memberRepository.findByNickname(nickname);
        if (member.isEmpty()) {
            throw new MemberNotFoundException();
        }
        Optional<MemberRecord> memberRecord = memberRecordRepository.findByMember(member.get());
        if (memberRecord.isEmpty()) {
            throw new MemberRecordNotFoundException();
        }
        return memberRecord.get();
    }

    @Override
    public MemberRecord updateMemberRecord(GameResultReq gameResultReq, String nickname) {
        GameResult gameResult = GameResult.valueOf(GameResult.class, gameResultReq.gameResult());

        // jwt로 정보 받아온 정보로 getMemberRecord 찾기
        MemberRecord memberRecord = getMemberRecord(nickname);

        // gameResult에 따라 업데이트
        if (GameResult.valueOf(GameResult.class, gameResultReq.gameResult()).equals(GameResult.WIN)) {
            memberRecord.win();
        } else if (GameResult.valueOf(GameResult.class, gameResultReq.gameResult()).equals(GameResult.LOSE)) {
            memberRecord.lose();
        }
        return memberRecord;
    }

    @Override
    public List<MemberRecord> getParticipants(String roomCode, String nickname) {

        List<MemberRecord> result = new ArrayList<>();
        String gameKey = GAME_MEMBER_KEY_PREFIX + roomCode;

        // 본인 전적
        Optional<Member> member = memberRepository.findByNickname(nickname);
        System.out.println(member.get().getNickname() + "--------------------------------------------");
        if (member.isEmpty()) {
            throw new MemberNotFoundException();
        }
        Optional<MemberRecord> MyRecord = memberRecordRepository.findByMember(member.get());
        if (MyRecord.isEmpty()) {
            throw new MemberRecordNotFoundException();
        }
        result.add(MyRecord.get());

        // 상대 전적
        GameMember gameMember = getGameMemberFromCache(roomCode);
        Set<GameMemberEssentialDTO> cachedUsers = gameMember.getMembers();
        Optional<GameMemberEssentialDTO> otherUser = findOtherUser(cachedUsers, nickname);

        // 상대가 없을 경우
        if (otherUser.isEmpty()) {
            result.add(null);
        }
        // 상대가 있을 경우
        else {
            Optional<Member> otherMember = memberRepository.findByNickname(otherUser.get().nickname());
            System.out.println(otherMember.get().getNickname() + "--------------------------------------------");
            if (otherMember.isEmpty()) {
                throw new MemberNotFoundException();
            }
            Optional<MemberRecord> otherRecord = memberRecordRepository.findByMember(otherMember.get());
            if (otherRecord.isEmpty()) {
                throw new MemberRecordNotFoundException();
            }
            result.add(otherRecord.get());
        }
        return result;
    }

    private Optional<GameMemberEssentialDTO> findOtherUser(Set<GameMemberEssentialDTO> cachedUser, String nickname) {
        return cachedUser.stream()
                .filter(dto -> !dto.nickname().equals(nickname))
                .findFirst();
    }

    private GameMember getGameMemberFromCache(String roomCode) {
        String gameMemberGen = redisTemplate.opsForValue().get(GAME_MEMBER_KEY_PREFIX + roomCode);
        return generateGameMember(gameMemberGen);
    }

    private GameMember generateGameMember(String gameMemberGen) {
        try {
            if (gameMemberGen != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(gameMemberGen, GameMember.class);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        throw new MemberNotFoundException();
    }

}
