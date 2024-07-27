package goormthon.team28.startup_valley.repository;

import goormthon.team28.startup_valley.domain.Member;
import goormthon.team28.startup_valley.domain.Team;
import goormthon.team28.startup_valley.domain.User;
import goormthon.team28.startup_valley.dto.type.EPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByIdAndUser(Long teamId, User user);
    Boolean existsByUser(User user);
    Boolean existsByUserAndTeam(User user, Team team);
    List<Member> findAllByTeam(Team team);
    List<Member> findAllByTeamAndPart(Team team, EPart part);
    List<Member> findAllByUser(User user);
    Optional<Member> findByTeamAndUser(Team team, User user);

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.totalMinute = :totalMinute where m.id = :memberId")
    void updateTotalMinute(Long memberId, Long totalMinute);

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.part = :part where m.id = :memberId")
    void updatePart(Long memberId, EPart part);

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.peerReviewSummary = :summary where m.id = :memberId")
    void updateReviewSummary(Long memberId, String summary);

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.retrospection = :retrospection where m.id = :memberId")
    void updateRetrospection(Long memberId, String retrospection);

    @Query(
            value = "SELECT m.id, " +
                    "COALESCE(w.counts, 0) " +
                    "FROM members m " +
                    "LEFT JOIN (" +
                    "SELECT w.member_id, COUNT(DISTINCT DATE(w.end_at)) AS counts " +
                    "FROM works w " +
                    "WHERE w.end_at IS NOT NULL " +
                    "AND DATE(w.end_at) >= :startDate " +
                    "GROUP BY w.member_id" +
                    ") AS w ON m.id = w.member_id " +
                    "WHERE m.team_id = :teamId " +
                    "ORDER BY counts DESC " +
                    "LIMIT 2",
            nativeQuery = true
    )
    List<Object[]> findAllByTeamAndStartDateOrderByWorkDay(Long teamId, LocalDate startDate);

    @Query(
            value = "SELECT m.id, " +
                    "COALESCE(w.times, 0) " +
                    "FROM members m " +
                    "LEFT JOIN (" +
                    "SELECT w.member_id, SUM(timestampdiff(minute, w.start_at, w.end_at)) AS times " +
                    "FROM works w " +
                    "WHERE w.end_at IS NOT NULL " +
                    "AND DATE(w.end_at) >= :startDate " +
                    "GROUP BY w.member_id" +
                    ") AS w ON m.id = w.member_id " +
                    "WHERE m.team_id = :teamId " +
                    "ORDER BY times DESC " +
                    "LIMIT 2",
            nativeQuery = true
    )
    List<Object[]> findByAllByTeamAndStartDateOrderByWorkTime(Long teamId, LocalDate startDate);

    @Query(
            value = "SELECT m.id, " +
                    "COALESCE(q.counts, 0) " +
                    "FROM members m " +
                    "LEFT JOIN (" +
                    "SELECT q.sender_id, COUNT(DATE(q.created_at)) AS counts " +
                    "FROM questions q " +
                    "WHERE DATE(q.created_at) >= :startDate " +
                    "GROUP BY q.sender_id" +
                    ") AS q ON m.id = q.sender_id " +
                    "WHERE m.team_id = :teamId " +
                    "ORDER BY counts DESC " +
                    "LIMIT 2",
            nativeQuery = true
    )
    List<Object[]> findByAllByTeamAndStartDateOrderByQuestionTimes(Long teamId, LocalDate startDate);

    @Query(
            value = "SELECT m.id, " +
                    "COALESCE(a.counts, 0) " +
                    "FROM members m " +
                    "LEFT JOIN (" +
                    "SELECT a.member_id, COUNT(DATE(a.created_at)) AS counts " +
                    "FROM answers a " +
                    "WHERE DATE(a.created_at) >= :startDate " +
                    "GROUP BY a.member_id" +
                    ") AS a ON m.id = a.member_id " +
                    "WHERE m.team_id = :teamId " +
                    "ORDER BY counts DESC " +
                    "LIMIT 2",
            nativeQuery = true
    )
    List<Object[]> findByAllByTeamAndStartDateOrderByFastAnswered(Long teamId, LocalDate startDate);

    @Query(
            value = "SELECT m.id, " +
                    "COALESCE(w.len, 0) " +
                    "FROM members m " +
                    "LEFT JOIN (" +
                    "SELECT w.member_id, SUM(LENGTH(w.content)) AS len " +
                    "FROM works w " +
                    "WHERE DATE(w.end_at) >= :startDate " +
                    "GROUP BY w.member_id" +
                    ") AS w ON m.id = w.member_id " +
                    "WHERE m.team_id = :teamId " +
                    "ORDER BY len DESC " +
                    "LIMIT 2",
            nativeQuery = true
    )
    List<Object[]> findByAllByTeamAndStartDateOrderByDetailedBacklog(Long teamId, LocalDate startDate);
}
