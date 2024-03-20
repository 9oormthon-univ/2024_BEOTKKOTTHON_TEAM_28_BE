package goormthon.team28.startup_valley.repository;

import goormthon.team28.startup_valley.domain.Member;
import goormthon.team28.startup_valley.domain.Scrum;
import goormthon.team28.startup_valley.domain.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {
    Optional<Work> findByScrumAndOwnerAndEndAtIsNull(Scrum scrum,Member owner);
    List<Work> findAllByOwner(Member member);
    List<Work> findTop3ByScrum(Scrum scrum);
}
