package goormthon.team28.startup_valley.repository;

import goormthon.team28.startup_valley.domain.Member;
import goormthon.team28.startup_valley.domain.Scrum;
import goormthon.team28.startup_valley.domain.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {
    Optional<Work> findByScrumAndOwnerAndEndAtIsNull(Scrum scrum,Member owner);
    Optional<Work> findByIdAndOwner(Long workId, Member owner);
    List<Work> findAllByOwner(Member member);
    List<Work> findAllByOwnerAndEndAtIsNotNull(Member member);
    List<Work> findAllByScrumOrderByEndAtDesc(Scrum scrum);
    List<Work> findAllByScrum(Scrum scrum);

    @Modifying(clearAutomatically = true)
    @Query("update Work w set w.content = :content, w.endAt = :now where w.id = :workId")
    void updateWorkAfterOver(Long workId, String content, LocalDateTime now);
}
