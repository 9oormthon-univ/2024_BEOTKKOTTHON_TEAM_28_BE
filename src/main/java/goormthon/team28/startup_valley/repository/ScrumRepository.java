package goormthon.team28.startup_valley.repository;

import goormthon.team28.startup_valley.domain.Member;
import goormthon.team28.startup_valley.domain.Scrum;
import goormthon.team28.startup_valley.dto.type.EScrumStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScrumRepository extends JpaRepository<Scrum, Long> {
    Optional<Scrum> findByWorkerAndStatus(Member worker, EScrumStatus status);
    List<Scrum> findAllByWorkerOrderByEndAtDesc(Member worker);
    @Modifying(clearAutomatically = true)
    @Query("update Scrum s set s.endAt = :end, s.status = :status, s.summary = :summary where s.id = :scrumId")
    void updateScrumAfterOver(Long scrumId, String summary, EScrumStatus status, LocalDate end);

}
