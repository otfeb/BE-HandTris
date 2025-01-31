package jungle.HandTris.global.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jungle.HandTris.application.service.MemberProfileService;
import jungle.HandTris.domain.GameMember;
import jungle.HandTris.domain.exception.DestinationUrlNotFoundException;
import jungle.HandTris.domain.exception.GameRoomNotFoundException;
import jungle.HandTris.domain.exception.MemberNotFoundException;
import jungle.HandTris.domain.repo.GameRoomRepository;
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
    private final MemberProfileService memberProfileService;
    private final GameRoomRepository gameRoomRepository;

    // UUID 추출을 위한 정규식 및 컴파일 상수
    private static final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
    private static final Pattern UUID_PATTERN = Pattern.compile(UUID_REGEX);
    private static final String GAME_MEMBER_KEY_PREFIX = "gameMember:";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {


        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//        if (isDisconnect(accessor))
//            return message;

        StompCommand commandType = accessor.getCommand();
        // 연결 해제 요청
        if (commandType == StompCommand.DISCONNECT) {
            return message;
        }
        String destinationUrl = accessor.getDestination();
        String jwtToken = extractJwtToken(accessor);
        String nickname = getNicknameFromToken(jwtToken);

        // 연결 요청
        if (commandType == StompCommand.CONNECT) {
            validateMemberExistence(nickname);
            accessor.setUser(new User(nickname));
        }

        // 발행 요청
        else if (commandType == StompCommand.SEND) {

            String roomCode = extractRoomCode(destinationUrl);
            GameMember gameMember = getGameMemberFromCache(roomCode);
            Set<GameMemberEssentialDTO> cachedUsers = gameMember.getMembers();

            // 방에 참가된 인원인지 확인
            if (!isUserInRoom(cachedUsers, nickname)) {
                throw new MemberNotFoundException();
            }
            // 탈주 발행 : 게임중 탈주처리를 위해 일반 발행보다 위에 존재
            if (destinationUrl.contains("/disconnect")) {
                GameMemberEssentialDTO disconnectedUser = findUser(cachedUsers, nickname);
                accessor.setHeader("User", disconnectedUser.nickname());

                // 2명인 방에서 탈주
                if (gameMember.gameMemberCount() == 2) {
                    GameMemberEssentialDTO otherUser = findOtherUser(cachedUsers, nickname);
                    accessor.setHeader("otherUser", otherUser.nickname());
                }
            }
            // 일반 발행
            else if (destinationUrl.equals("/app/" + roomCode + "/tetris")) {
                GameMemberEssentialDTO otherUser = findOtherUser(cachedUsers, nickname);
                accessor.setHeader("otherUser", otherUser.nickname());
            }


        }
        return message;
    }


    // 함수들 -----------------------------------------------------------------------------------------------------------

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
        return generateGameMember(gameMemberGen);
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

    private boolean isUserInRoom(Set<GameMemberEssentialDTO> cachedUser, String nickname) {
        return cachedUser.stream()
                .anyMatch(dto -> dto.nickname().equals(nickname));
    }

    private boolean isDisconnect(StompHeaderAccessor accessor) {
        StompCommand commandType = accessor.getCommand();
        return commandType == StompCommand.DISCONNECT;
    }
}
