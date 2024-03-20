package goormthon.team28.startup_valley.service;

import goormthon.team28.startup_valley.domain.Answer;
import goormthon.team28.startup_valley.domain.Member;
import goormthon.team28.startup_valley.domain.Question;
import goormthon.team28.startup_valley.domain.User;
import goormthon.team28.startup_valley.dto.request.AnswerCreateDto;
import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import goormthon.team28.startup_valley.repository.AnswerRepository;
import goormthon.team28.startup_valley.repository.MemberRepository;
import goormthon.team28.startup_valley.repository.QuestionRepository;
import goormthon.team28.startup_valley.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;


    @Transactional
    public Boolean postAnswer(Long userId, AnswerCreateDto answerCreateDto) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Member currentMember = memberRepository.findByIdAndUser(answerCreateDto.memberId(), currentUser)
                .orElseThrow(() -> new CommonException(ErrorCode.MISMATCH_LOGIN_USER_AND_MEMBER));
        Question question = questionRepository.findById(answerCreateDto.questionId()).
                orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_QUESTION));
        Answer answer = Answer.builder()
                .question(question)
                .member(currentMember)
                .content(answerCreateDto.content())
                .createdAt(LocalDateTime.now())
                .build();

        answerRepository.save(answer);

        return Boolean.TRUE;
    }

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
