package goormthon.team28.startup_valley.repository;

import goormthon.team28.startup_valley.domain.Member;
import goormthon.team28.startup_valley.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    @Modifying(clearAutomatically = true)
    @Query("update Team t set t.leader = :leader where t.id = :teamId")
    void updateLeader(Long teamId, Member leader);

}
