package goormthon.team28.startup_valley.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import goormthon.team28.startup_valley.domain.Member;
import goormthon.team28.startup_valley.domain.Review;
import goormthon.team28.startup_valley.domain.Team;
import goormthon.team28.startup_valley.domain.User;
import goormthon.team28.startup_valley.dto.request.ReviewCreateDto;
import goormthon.team28.startup_valley.dto.response.PeerReviewDto;
import goormthon.team28.startup_valley.dto.response.PeerReviewListDto;
import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import goormthon.team28.startup_valley.repository.MemberRepository;
import goormthon.team28.startup_valley.repository.ReviewRepository;
import goormthon.team28.startup_valley.repository.TeamRepository;
import goormthon.team28.startup_valley.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final GptService gptService;
    @Transactional
    public void saveReview(Team team, Member sender, Member receiver, String content){
        reviewRepository.save(Review.builder()
                .team(team)
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .build()
        );
    }

    public PeerReviewListDto listPeerReview(Long userId, Long teamsId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Team team = teamRepository.findById(teamsId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TEAM));
        Member currentMember = memberRepository.findByTeamAndUser(team, currentUser)
                .orElseThrow(() -> new CommonException(ErrorCode.MISMATCH_LOGIN_USER_AND_TEAM));

        List<Member> memberList = memberRepository.findAllByTeam(team);
        List<PeerReviewDto> peerReviewDtoList = memberList.stream()
                .filter(member -> !member.equals(currentMember))
                .map(member -> PeerReviewDto.of(
                        member.getId(),
                        member.getUser().getNickname(),
                        member.getUser().getProfileImage(),
                        member.getPart(),
                        reviewRepository.findBySenderAndReceiver(currentMember, member)
                                .map(Review::getContent)
                                .orElse(null)
                ))
                .toList();

        return PeerReviewListDto.of(team.getName(), peerReviewDtoList);
    }
    @Transactional
    public Boolean postPeerReview(Long userId, Long teamsId, ReviewCreateDto reviewCreateDto) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Team team = teamRepository.findById(teamsId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TEAM));
        // 로그인 유저가 해당 팀에 속하는 지 확인
        Member currentMember = memberRepository.findByTeamAndUser(team, currentUser)
                .orElseThrow(() -> new CommonException(ErrorCode.MISMATCH_LOGIN_USER_AND_TEAM));
        // 리뷰를 받을 유저의 존재 유무확인
        Member targetMember = memberRepository.findById(reviewCreateDto.memberId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
        // 자신에게 리뷰하는 예외 처리
        if (currentMember.equals(targetMember))
            throw new CommonException(ErrorCode.INVALID_CREATE_PEER_REVIEW_SELF);
        // 이미 리뷰를 작성한 경우 예외 처리
        if (reviewRepository.existsBySender(currentMember))
            throw new CommonException(ErrorCode.INVALID_CREATE_PEER_REVIEW_OTHER);
        // 서로 팀이 다른 경우
        if (!targetMember.getTeam().equals(team))
            throw new CommonException(ErrorCode.INVALID_CREATE_PEER_REVIEW_TO_OTHER_TEAM_MEMBER);

        Review review = Review.builder()
                              .team(team)
                              .sender(currentMember)
                              .receiver(targetMember)
                              .content(reviewCreateDto.content())
                              .build();
        reviewRepository.save(review);

        List<Member> teamMemberList = memberRepository.findAllByTeam(team);
        List<Review> reviewList = reviewRepository.findAllByReceiver(targetMember);
        // 자신을 제외한 모든 팀원들에게 동료 평가를 받은 경우
        if (teamMemberList.size() == reviewList.size() + 1) {
            List<String> reviewStringList = reviewList.stream()
                    .map(Review::getContent)
                    .toList();
            String peerReviewSummary = null;
            try {
                 peerReviewSummary = gptService.sendMessage(reviewStringList, false);
            } catch (JsonProcessingException e) {
                throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
            } finally {
                targetMember.updatePeerReviewSummary(peerReviewSummary);
            }
        }

        return Boolean.TRUE;
    }
    public boolean isAlreadyExistReview(Member sender, Member receiver){
        return reviewRepository.existsBySenderAndReceiver(sender, receiver);
    }
    public List<Review> findAllByReceiver(Member receiver){
        return reviewRepository.findAllByReceiver(receiver);
    }
}
