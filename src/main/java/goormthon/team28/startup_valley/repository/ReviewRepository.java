package goormthon.team28.startup_valley.repository;

import goormthon.team28.startup_valley.domain.Review;
import goormthon.team28.startup_valley.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByTeam(Team team);
}
