package jungle.HandTris.domain;

import jungle.HandTris.presentation.dto.request.GameMemberEssentialDTO;
import jungle.HandTris.presentation.dto.response.MemberRecordDetailRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.LinkedHashSet;
import java.util.Set;

@RedisHash(value = "gameMember")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameMember {


    @Id
    private String id;

    private Set<GameMemberEssentialDTO> members = new LinkedHashSet<>();

    public GameMember(String id) {
        this.id = id;
    }

    public GameMember(String nickname, String profileImageUrl, MemberRecordDetailRes recordDetail) {
    }

    public int gameMemberCount() {
        return this.members.size();
    }

    public void addMember(GameMemberEssentialDTO dto) {
        this.members.add(dto);
    }

    public void removeMember(String nickname) {

        GameMemberEssentialDTO foundDTO = this.members.stream().filter(gameMemberEssentialDTO -> gameMemberEssentialDTO.nickname().equals(nickname)).findAny().orElse(null);
        this.members.remove(foundDTO);
    }

    public boolean isPresentMember(String nickname) {

        GameMemberEssentialDTO foundDTO = this.members.stream().filter(gameMemberEssentialDTO -> gameMemberEssentialDTO.nickname().equals(nickname)).findAny().orElse(null);
        return foundDTO != null;
    }

    public GameMember(String id, GameMemberEssentialDTO member) {
        this.id = id;
        this.members.add(member);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"id\":\"").append(id).append("\", \"members\":[");

        for (GameMemberEssentialDTO ge : members) {
            sb.append(ge.toString());
            sb.append(",");
        }
        if (!members.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1); // 마지막 쉼표 제거
        }
        sb.append("]}");
        return sb.toString();
    }
}
