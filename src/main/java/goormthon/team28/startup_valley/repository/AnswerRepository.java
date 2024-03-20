package goormthon.team28.startup_valley.repository;

import goormthon.team28.startup_valley.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
