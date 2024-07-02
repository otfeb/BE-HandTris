package jungle.HandTris.domain.repo;

import jungle.HandTris.domain.GameMember;
import org.springframework.data.repository.CrudRepository;

public interface GameMemberRepository extends CrudRepository<GameMember, String> {
}
