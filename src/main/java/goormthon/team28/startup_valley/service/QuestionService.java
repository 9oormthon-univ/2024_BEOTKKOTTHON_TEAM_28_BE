package goormthon.team28.startup_valley.service;


import goormthon.team28.startup_valley.domain.Member;
import goormthon.team28.startup_valley.domain.Question;
import goormthon.team28.startup_valley.domain.User;
import goormthon.team28.startup_valley.dto.response.QuestionDto;
import goormthon.team28.startup_valley.dto.response.QuestionListDto;
import goormthon.team28.startup_valley.dto.type.EQuestionStatus;
import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import goormthon.team28.startup_valley.repository.MemberRepository;
import goormthon.team28.startup_valley.repository.QuestionRepository;
import goormthon.team28.startup_valley.repository.TeamRepository;
import goormthon.team28.startup_valley.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;

    public QuestionListDto listWaitingQuestion(Long userId, Long teamsId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        if (teamRepository.existsById(teamsId))
            throw new CommonException(ErrorCode.NOT_FOUND_TEAM);

        Member member = memberRepository.findByIdAndUser(teamsId, currentUser)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));

        List<Question> questionList = questionRepository
                .findByReceiverAndStatus(member, EQuestionStatus.WAITING_ANSWER);
        List<QuestionDto> questionDtoList = questionList.stream()
                .map(question -> QuestionDto.of(
                        question.getId(),
                        question.getContent(),
                        question.getReceiver().getPart(),
                        question.getSender().getUser().getProfileImage()
                )).toList();

        return QuestionListDto.of(questionDtoList, questionList.size());
    }
}
