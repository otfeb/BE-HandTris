package jungle.HandTris.domain.repo;

import jungle.HandTris.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    @Query("SELECT g FROM Game g WHERE g.participantCount < g.participantLimit AND g.gameStatus != 'PLAYING' ")
    List<Game> findAllByGameStatusNotPlaying();

}
