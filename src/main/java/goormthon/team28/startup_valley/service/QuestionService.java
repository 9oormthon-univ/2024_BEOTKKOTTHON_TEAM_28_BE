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
    private final TeamService teamService;
    private final MemberService memberService;
    private final UserService userService;
    @Transactional
    public Question saveQuestion(String guildId, String senderId, String receiverId, String content, LocalDateTime time){
        String code = NumberUtil.generateRandomCode();
        while(questionRepository.existsByCode(code)){
            code = NumberUtil.generateRandomCode();
        }
        Team findTeam = teamService.findByGuildId(guildId).get();
        return questionRepository.save(Question.builder()
                        .sender(memberService.findByTeamAndUser(findTeam, userService.findBySerialId(senderId).get()))
                        .receiver(memberService.findByTeamAndUser(findTeam, userService.findBySerialId(receiverId).get()))
                        .content(content)
                        .status(EQuestionStatus.WAITING_ANSWER)
                        .createdAt(time)
                        .code(code)
                .build()
        );
    }
    public Question findByCode(String code){
        return questionRepository.findByCodeAndStatus(code,EQuestionStatus.WAITING_ANSWER)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_QUESTION));
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

    public QuestionRetrieveSetListDto listReceivedQuestion(Long userId, Long teamsId, Boolean isReceived) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Team team = teamRepository.findById(teamsId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TEAM));
        Member member = memberRepository.findByTeamAndUser(team, currentUser)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));

        List<Question> questionList = isReceived ? questionRepository .findAllByReceiver(member)
                                                 : questionRepository.findAllBySender(member);
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
                                    .content(LocalDateTime.now().toString())
                                    .status(EQuestionStatus.WAITING_ANSWER)
                                    .code(code)
                                    .build();
        questionRepository.save(question);

        return Boolean.TRUE;
    }
}
