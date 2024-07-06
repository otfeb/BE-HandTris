package jungle.HandTris.global.handler;

import jungle.HandTris.domain.exception.DestinationUrlNotFoundException;
import jungle.HandTris.domain.exception.GameRoomNotFoundException;
import jungle.HandTris.domain.exception.MemberNotFoundException;
import jungle.HandTris.domain.repo.MemberRepository;
import jungle.HandTris.global.config.ws.User;
import jungle.HandTris.global.jwt.JWTUtil;
import jungle.HandTris.presentation.dto.request.RoomMemberNicknameDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Optional;
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
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        StompCommand commandType = accessor.getCommand();
        String destinationUrl = accessor.getDestination();
        String jwtToken = accessor.getFirstNativeHeader("Authorization");

        if (StompCommand.CONNECT.equals(commandType)) {
            if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
                jwtToken = jwtToken.substring(7);
            }
            String tokenNickname = jwtUtil.getNickname(jwtToken);
            accessor.setUser(new User(tokenNickname));

        } else if (commandType == StompCommand.SUBSCRIBE) {
            // Url에서 roodCode 추출
            if (destinationUrl == null) {
                throw new DestinationUrlNotFoundException();
            }

            Matcher matcher = UUID_PATTERN.matcher(destinationUrl);

            if (!matcher.find()) {
                throw new GameRoomNotFoundException();
            }
            String roomCode = matcher.group();

            Optional<RoomMemberNicknameDTO> optionalCacheUser = Optional.ofNullable(roomMap.get(roomCode));
            RoomMemberNicknameDTO cachedUser = null;

            if (optionalCacheUser.isEmpty()) {
                cachedUser = new RoomMemberNicknameDTO();
                roomMap.put(roomCode, cachedUser);
            }

            if (roomCode == null) {
                throw new GameRoomNotFoundException();
            }
            if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
                jwtToken = jwtToken.substring(7);
            }
            String nickname = jwtUtil.getNickname(jwtToken);

            if (nickname.equals(memberRepository.findByNickname(nickname).orElseThrow().getNickname())) {
                Optional<RoomMemberNicknameDTO> cuser = Optional.ofNullable(roomMap.get(roomCode));
                if (cuser.isEmpty()) {
                    cachedUser.addNickname(nickname);
                    roomMap.put(roomCode, cachedUser);
                }

                if (!cuser.get().getNicknames().contains(nickname)) {
                    // 매핑 저장
                    cuser.get().addNickname(nickname);
                    roomMap.put(roomCode, cuser.get()
                    );
                }
            } else {
                throw new MemberNotFoundException();
            }
        }

        // 발행 요청
        else if (commandType == StompCommand.SEND) {
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

            if (destinationUrl.contains("start")) {
                RoomMemberNicknameDTO roomMemberNicknameDTO = roomMap.get(roomCode);
                roomMemberNicknameDTO.getNicknames().forEach(System.out::println);
            }

            String nickname = jwtUtil.getNickname(jwtToken);

            RoomMemberNicknameDTO cachedUser = roomMap.get(roomCode);

            // 해당 닉네임 없으면 예외처리
            if (!cachedUser.containsNickname(nickname)) {
                throw new MemberNotFoundException();
            }

            if (destinationUrl.equals("/app/" + roomCode + "/tetris")) {
                // Sender가 아닌 유저 찾기
                String otherUser = cachedUser.getNicknames().stream()
                        .filter(nick -> !nick.equals(nickname))
                        .findFirst()
                        .orElseThrow(MemberNotFoundException::new);

                accessor.setHeader("otherUser", otherUser);
            }
        }
//        else if (commandType == StompCommand.DISCONNECT || commandType == StompCommand.UNSUBSCRIBE) {
//            System.out.println("ㅇㅇㅇㅇㅇㅇㅇㅇ" + destinationUrl);
//            Matcher matcher = UUID_PATTERN.matcher(destinationUrl);
//            String roomCode = matcher.find() ? matcher.group() : null;
//
//            if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
//                jwtToken = jwtToken.substring(7);
//            }
//            String nickname = jwtUtil.getNickname(jwtToken);
//            RoomMemberNicknameDTO cachedUser = roomMap.get(roomCode);
//
//            if (roomMap.get(roomCode).getNicknames().size() == 1) {
//                roomMap.remove(roomCode);
//            } else if (roomMap.get(roomCode).getNicknames().size() == 2) {
//                roomMap.get(roomCode).getNicknames().remove(nickname);
//            }
//        }
//        return MessageBuilder.createMessage(message, accessor.getMessageHeaders());
        return message;
    }
}
