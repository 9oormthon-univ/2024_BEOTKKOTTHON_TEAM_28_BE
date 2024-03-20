package goormthon.team28.startup_valley.repository;

import goormthon.team28.startup_valley.domain.Member;
import goormthon.team28.startup_valley.domain.Question;
import goormthon.team28.startup_valley.dto.type.EQuestionStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @EntityGraph(attributePaths = {"sender"})
    Optional<Question> findByCodeAndStatus(String code, EQuestionStatus status);
    List<Question> findAllByReceiverAndStatus(Member member, EQuestionStatus eQuestionStatus);
    boolean existsByCode(String code);
    @Modifying(clearAutomatically = true)
    @Query("update Question q set q.status = :status, q.code = :code where q.id = :questionId")
    void updateQuestionStatus(Long questionId, String code, EQuestionStatus status);
}
