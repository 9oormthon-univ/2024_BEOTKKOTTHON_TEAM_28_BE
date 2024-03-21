package goormthon.team28.startup_valley.repository;

import goormthon.team28.startup_valley.domain.Answer;
import goormthon.team28.startup_valley.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByQuestion(Question question);
}
