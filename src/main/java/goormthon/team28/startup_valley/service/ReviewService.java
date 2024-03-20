package goormthon.team28.startup_valley.service;

import goormthon.team28.startup_valley.domain.Member;
import goormthon.team28.startup_valley.domain.Review;
import goormthon.team28.startup_valley.domain.Team;
import goormthon.team28.startup_valley.domain.User;
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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

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
                        reviewRepository.findBySender(member)
                                .map(Review::getContent)
                                .orElse(null)
                ))
                .toList();

        return PeerReviewListDto.of(team.getName(), peerReviewDtoList);
    }
}
