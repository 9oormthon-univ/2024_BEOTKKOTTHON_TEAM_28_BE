package goormthon.team28.startup_valley.repository;

import goormthon.team28.startup_valley.domain.Member;
import goormthon.team28.startup_valley.domain.Scrum;
import goormthon.team28.startup_valley.dto.type.EScrumStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScrumRepository extends JpaRepository<Scrum, Long> {
    Optional<Scrum> findByWorkerAndStatus(Member worker, EScrumStatus status);
    List<Scrum> findAllByWorkerOrderByEndAtDesc(Member worker);


}
