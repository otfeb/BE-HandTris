package jungle.HandTris.global.handler;

import jungle.HandTris.domain.exception.DestinationUrlNotFoundException;
import jungle.HandTris.domain.exception.GameRoomNotFoundException;
import jungle.HandTris.domain.exception.InvalidTokenFormatException;
import jungle.HandTris.domain.exception.MemberNotFoundException;
import jungle.HandTris.domain.repo.MemberRepository;
import jungle.HandTris.global.jwt.JWTUtil;
import jungle.HandTris.presentation.dto.request.RoomMemberNicknameDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;
    private final ConcurrentHashMap<String, RoomMemberNicknameDTO> roomMap = new ConcurrentHashMap<>();

    // UUID 추출을 위한 정규식 및 컴파일 상수
    private static final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
    private static final Pattern UUID_PATTERN = Pattern.compile(UUID_REGEX);

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand commandType = accessor.getCommand();
        String destinationUrl = accessor.getDestination();
        String jwtToken = accessor.getFirstNativeHeader("Authorization");
        
        // Url에서 roodCode 추출
        if (destinationUrl == null) {
            throw new DestinationUrlNotFoundException();
        }

        Matcher matcher = UUID_PATTERN.matcher(destinationUrl);
        String roomCode = matcher.find() ? matcher.group() : null;

        if (roomCode == null) {
            throw new GameRoomNotFoundException();
        }

        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);
        }

        String nickname = jwtUtil.getNickname(jwtToken);

        if (StompCommand.CONNECT == commandType) {
            System.out.println("Connect");
            // 토큰 유효성 확인
            if (jwtToken != null && nickname.equals(memberRepository.findByNickname(nickname).orElseThrow().getNickname()) && !jwtUtil.isExpired(jwtToken)) {
                RoomMemberNicknameDTO cachedUser = roomMap.get(roomCode);
                // 첫 연결 때 저장
                if (cachedUser == null) {
                    cachedUser = new RoomMemberNicknameDTO();
                    cachedUser.addNickname(nickname);
                    roomMap.put(roomCode, cachedUser);
                } else {
                    // 해당 닉네임 없으면 예외처리
                    if (!cachedUser.containsNickname(nickname)) {
                        throw new MemberNotFoundException();
                    }
                }
            } else {
                throw new InvalidTokenFormatException();
            }
        } else if (StompCommand.SEND == commandType) {
            RoomMemberNicknameDTO cachedUser = roomMap.get(roomCode);

            if (cachedUser == null || !cachedUser.containsNickname(nickname)) {
                throw new MemberNotFoundException();
            }

            if (destinationUrl.equals("/app/" + roomCode + "/tetris")) {
                cachedUser = roomMap.get(roomCode);

                // Sender가 아닌 유저 찾기
                String otherUser = cachedUser.getNicknames().stream()
                        .filter(nick -> !nick.equals(nickname))
                        .findFirst()
                        .orElseThrow(MemberNotFoundException::new);

                accessor.setHeader("otherUser", otherUser);
            }
        }
        return message;
    }
}
