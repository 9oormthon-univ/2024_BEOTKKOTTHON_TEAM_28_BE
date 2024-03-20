package goormthon.team28.startup_valley.repository;

import goormthon.team28.startup_valley.domain.Member;
import goormthon.team28.startup_valley.domain.Team;
import goormthon.team28.startup_valley.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByIdAndUser(Long teamId, User user);
    Boolean existsByUser(User user);
    Boolean existsByUserAndTeam(User user, Team team);
    List<Member> findAllByTeam(Team team);
    List<Member> findAllByTeamOrderByTotalMinuteDesc(Team team);
    List<Member> findAllByUser(User user);
    Optional<Member> findByTeamAndUser(Team team, User user);
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.totalMinute = :totalMinute where m.id = :memberId")
    void updateTotalMinute(Long memberId, Long totalMinute);
}
