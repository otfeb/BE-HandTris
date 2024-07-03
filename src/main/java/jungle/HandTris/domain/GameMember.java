package jungle.HandTris.domain;

import jungle.HandTris.presentation.dto.request.GameMemberEssentialDTO;
import jungle.HandTris.presentation.dto.response.MemberRecordDetailRes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.LinkedHashSet;
import java.util.Set;

@RedisHash(value = "gameMember")
@Getter
@NoArgsConstructor
public class GameMember {


    @Id
    private String id;

    private Set<GameMemberEssentialDTO> members = new LinkedHashSet<>(); // HashSet -> Linked HashSet

    public GameMember(String id) {
        this.id = id;
    }

    public GameMember(String nickname, String first, MemberRecordDetailRes second) {
    }

    public int gameMemberCount() {
        return this.members.size();
    }

    public void addMember(GameMemberEssentialDTO dto) {
        this.members.add(dto); // this 붙여야 자기 접근자가 멀티 스레드 환경에서 정상적으로 동작
    }

    public void removeMember(GameMemberEssentialDTO dto) {
        this.members.remove(dto);
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
