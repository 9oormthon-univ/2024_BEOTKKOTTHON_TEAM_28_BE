package goormthon.team28.startup_valley.service;

import goormthon.team28.startup_valley.domain.Answer;
import goormthon.team28.startup_valley.domain.Question;
import goormthon.team28.startup_valley.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    @Transactional
    public Answer saveAnswer(Question question, String content, LocalDateTime now){
        return answerRepository.save(Answer.builder()
                        .question(question)
                        .content(content)
                        .createdAt(now)
                .build()
        );
    }
}
