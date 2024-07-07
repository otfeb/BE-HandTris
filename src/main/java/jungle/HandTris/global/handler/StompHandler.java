package jungle.HandTris.global.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jungle.HandTris.domain.GameMember;
import jungle.HandTris.domain.exception.DestinationUrlNotFoundException;
import jungle.HandTris.domain.exception.GameRoomNotFoundException;
import jungle.HandTris.domain.exception.MemberNotFoundException;
import jungle.HandTris.domain.repo.MemberRepository;
import jungle.HandTris.global.config.ws.User;
import jungle.HandTris.global.jwt.JWTUtil;
import jungle.HandTris.presentation.dto.request.GameMemberEssentialDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    // UUID 추출을 위한 정규식 및 컴파일 상수
    private static final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
    private static final Pattern UUID_PATTERN = Pattern.compile(UUID_REGEX);
    private static final String GAME_MEMBER_KEY_PREFIX = "gameMember:";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        StompCommand commandType = accessor.getCommand();
        String destinationUrl = accessor.getDestination();
        String jwtToken = extractJwtToken(accessor);

        // 연결 요청
        if (commandType == StompCommand.CONNECT) {
            String nickname = getNicknameFromToken(jwtToken);
            accessor.setUser(new User(nickname));
            validateMemberExistence(nickname);
        }

        // 발행 요청
        else if (commandType == StompCommand.SEND) {
            String roomCode = extractRoomCode(destinationUrl);
            String nickname = getNicknameFromToken(jwtToken);
            GameMember gameMember = getGameMemberFromCache(roomCode);

            if (findUser(gameMember.getMembers(), nickname) == null) {
                throw new MemberNotFoundException();
            }
            Set<GameMemberEssentialDTO> cachedUser = gameMember.getMembers();
            if (destinationUrl.equals("/app/" + roomCode + "/tetris")) {
                // Sender가 아닌 유저 찾기
                GameMemberEssentialDTO otherUser = findOtherUser(cachedUser, nickname);
                accessor.setHeader("otherUser", otherUser.nickname());
            } else if (destinationUrl.contains("/disconnect")) {
                GameMemberEssentialDTO otherUser = findOtherUser(cachedUser, nickname);
                GameMemberEssentialDTO disconnectedUser = findUser(cachedUser, nickname);
                accessor.setHeader("otherUser", otherUser.nickname());
                gameMember.removeMember(disconnectedUser);
                if (gameMember.gameMemberCount() != 0) {
                    redisTemplate.delete(GAME_MEMBER_KEY_PREFIX + roomCode);
                    redisTemplate.opsForValue().set(GAME_MEMBER_KEY_PREFIX + roomCode, gameMember.toString());
                } else {
                    redisTemplate.delete(GAME_MEMBER_KEY_PREFIX + roomCode);
                }
            }
        }
        return message;
    }

    private String extractJwtToken(StompHeaderAccessor accessor) {
        String jwtToken = accessor.getFirstNativeHeader("Authorization");
        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            return jwtToken.substring(7);
        }
        return null;
    }

    private String extractRoomCode(String destinationUrl) {
        if (destinationUrl == null) {
            throw new DestinationUrlNotFoundException();
        }
        Matcher matcher = UUID_PATTERN.matcher(destinationUrl);
        if (!matcher.find()) {
            throw new GameRoomNotFoundException();
        }
        return matcher.group();
    }

    private String getNicknameFromToken(String jwtToken) {
        if (jwtToken != null) {
            return jwtUtil.getNickname(jwtToken);
        }
        throw new MemberNotFoundException();
    }

    private void validateMemberExistence(String nickname) {
        memberRepository.findByNickname(nickname).orElseThrow(MemberNotFoundException::new);
    }

    private GameMember getGameMemberFromCache(String roomCode) {
        String gameMemberGen = redisTemplate.opsForValue().get(GAME_MEMBER_KEY_PREFIX + roomCode);
        return Optional.ofNullable(generateGameMember(gameMemberGen))
                .orElseThrow(MemberNotFoundException::new);
    }

    private GameMemberEssentialDTO findOtherUser(Set<GameMemberEssentialDTO> cachedUser, String nickname) {
        return cachedUser.stream()
                .filter(dto -> !dto.nickname().equals(nickname))
                .findFirst()
                .orElseThrow(MemberNotFoundException::new);
    }

    private GameMemberEssentialDTO findUser(Set<GameMemberEssentialDTO> cachedUser, String nickname) {
        return cachedUser.stream()
                .filter(dto -> dto.nickname().equals(nickname))
                .findFirst()
                .orElseThrow(MemberNotFoundException::new);
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
