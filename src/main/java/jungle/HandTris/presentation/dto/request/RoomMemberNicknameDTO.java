package jungle.HandTris.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoomMemberNicknameDTO {
    private Set<String> nicknames = new HashSet<>();

    public void addNickname(String nickname) {
        nicknames.add(nickname);
    }

    public boolean containsNickname(String nickname) {
        return nicknames.contains(nickname);
    }
}
