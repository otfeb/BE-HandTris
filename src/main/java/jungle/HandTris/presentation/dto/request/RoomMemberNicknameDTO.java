package jungle.HandTris.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public class RoomMemberNicknameDTO {
    private Set<String> nicknames;

    public RoomMemberNicknameDTO() {
        this.nicknames = new HashSet<>();
    }

    public void addNickname(String nickname) {
        this.nicknames.add(nickname);
    }

    public boolean containsNickname(String nickname) {
        return this.nicknames.contains(nickname);
    }
}
