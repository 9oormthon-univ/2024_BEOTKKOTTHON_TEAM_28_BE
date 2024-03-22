package goormthon.team28.startup_valley.service;


import goormthon.team28.startup_valley.domain.*;
import goormthon.team28.startup_valley.dto.request.QuestionCreateDto;
import goormthon.team28.startup_valley.dto.response.*;
import goormthon.team28.startup_valley.dto.type.EQuestionStatus;
import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import goormthon.team28.startup_valley.repository.*;
import goormthon.team28.startup_valley.util.NumberUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    @Transactional
    public Question saveQuestion(Member sender, Member receiver, String content, LocalDateTime time){
        String code = NumberUtil.generateRandomCode();
        while(questionRepository.existsByCode(code)){
            code = NumberUtil.generateRandomCode();
        }
        return questionRepository.save(Question.builder()
                        .sender(sender)
                        .receiver(receiver)
                        .content(content)
                        .status(EQuestionStatus.WAITING_ANSWER)
                        .createdAt(time)
                        .code(code)
                .build()
        );
    }
    public Optional<Question> findByCode(String code){
        return questionRepository.findByCodeAndStatus(code,EQuestionStatus.WAITING_ANSWER);
    }
    @Transactional
    public void updateQuestionStatus(Question question,EQuestionStatus status){
        questionRepository.updateQuestionStatus(question.getId(), null, status);
    }

    public QuestionListDto listWaitingQuestion(Long userId, Long teamsId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        if (!teamRepository.existsById(teamsId))
            throw new CommonException(ErrorCode.NOT_FOUND_TEAM);

        Member member = memberRepository.findByIdAndUser(teamsId, currentUser)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));

        List<Question> questionList = questionRepository
                .findAllByReceiverAndStatus(member, EQuestionStatus.WAITING_ANSWER);
        List<QuestionDto> questionDtoList = questionList.stream()
                .map(question -> QuestionDto.of(
                        question.getId(),
                        question.getContent(),
                        question.getReceiver().getPart(),
                        question.getSender().getUser().getProfileImage()
                )).toList();

        return QuestionListDto.of(questionDtoList, questionDtoList.size());
    }

    public QuestionRetrieveSetListDto listReceivedQuestion(
            Long userId,
            Long teamsId,
            Boolean isReceived,
            String sort
    ) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Team team = teamRepository.findById(teamsId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TEAM));
        Member member = memberRepository.findByTeamAndUser(team, currentUser)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));

        List<Question> questionList;
        switch (sort) {
            case "all" -> questionList = isReceived ? questionRepository.findAllByReceiver(member)
                    : questionRepository.findAllBySender(member);
            case "completed" -> questionList = isReceived ?
                    questionRepository.findAllByReceiverAndStatus(member, EQuestionStatus.FINISH) :
                    questionRepository.findAllBySenderAndStatus(member, EQuestionStatus.FINISH);
            case "pending" -> questionList = isReceived ?
                    questionRepository.findAllByReceiverAndStatus(member, EQuestionStatus.WAITING_ANSWER) :
                    questionRepository.findAllBySenderAndStatus(member, EQuestionStatus.WAITING_ANSWER);
            default -> throw new CommonException(ErrorCode.INVALID_QUERY_PARAMETER);
        }
        List<QuestionRetrieveSetDto> questionRetrieveSetDtoList = new ArrayList<>();
        for (Question question : questionList) {
            Optional<Answer> answer = answerRepository.findByQuestion(question);
            questionRetrieveSetDtoList.add(QuestionRetrieveSetDto.of(
                    QuestionRetrieveDto.of(
                            question.getId(),
                            currentUser.getNickname(),
                            currentUser.getProfileImage(),
                            member.getPart(),
                            question.getContent(),
                            question.getCreatedAt()
                    ),
                    answer.map(value -> QuestionRetrieveDto.of(
                            value.getId(),
                            value.getMember().getUser().getNickname(),
                            value.getMember().getUser().getProfileImage(),
                            value.getMember().getPart(),
                            value.getContent(),
                            value.getCreatedAt()
                    )).orElse(null)
            ));
        }

        return QuestionRetrieveSetListDto.of(questionRetrieveSetDtoList);
    }

    @Transactional
    public Boolean postQuestion(Long userId, Long teamsId, QuestionCreateDto questionCreateDto) {

        User senderUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Team senderTeam = teamRepository.findById(teamsId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TEAM));
        Member senderMember = memberRepository.findByTeamAndUser(senderTeam, senderUser)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
        Member receiverMember = memberRepository.findById(questionCreateDto.memberId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
        if (!senderMember.getTeam().equals(receiverMember.getTeam()))
            throw new CommonException(ErrorCode.MISMATCH_TEAM);

        String code = NumberUtil.generateRandomCode();
        while(questionRepository.existsByCode(code))
            code = NumberUtil.generateRandomCode();
        Question question = Question.builder()
                                    .sender(senderMember)
                                    .receiver(receiverMember)
                                    .content(questionCreateDto.content())
                                    .createdAt(LocalDateTime.now())
                                    .status(EQuestionStatus.WAITING_ANSWER)
                                    .code(code)
                                    .build();
        questionRepository.save(question);

        return Boolean.TRUE;
    }
}
