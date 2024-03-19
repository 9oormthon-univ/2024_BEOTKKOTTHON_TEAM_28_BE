package goormthon.team28.startup_valley.repository;

import goormthon.team28.startup_valley.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

}
