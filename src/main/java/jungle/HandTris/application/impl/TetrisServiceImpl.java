package jungle.HandTris.application.impl;

import jungle.HandTris.application.service.TetrisService;
import jungle.HandTris.domain.GameMember;
import jungle.HandTris.domain.repo.GameMemberRepository;
import jungle.HandTris.presentation.dto.response.RoomOwnerRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TetrisServiceImpl implements TetrisService {

    private final GameMemberRepository gameMemberRepository;

    public RoomOwnerRes checkRoomOwnerAndReady(String roomCode) {
        GameMember byId = gameMemberRepository.findById(roomCode).orElseThrow();

        if (byId.gameMemberCount() == 1) {
            return new RoomOwnerRes(true);
        }
        return new RoomOwnerRes(false);
    }
}
