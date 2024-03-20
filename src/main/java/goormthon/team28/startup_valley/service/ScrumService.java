package goormthon.team28.startup_valley.service;

import goormthon.team28.startup_valley.domain.Member;
import goormthon.team28.startup_valley.domain.Scrum;
import goormthon.team28.startup_valley.dto.type.EScrumStatus;
import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import goormthon.team28.startup_valley.repository.ScrumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScrumService {
    private final ScrumRepository scrumRepository;
    @Transactional
    public Scrum saveScrum(Member member, LocalDate now){
        return scrumRepository.findByWorkerAndStatus(member, EScrumStatus.IN_PROGRESS)
                .orElseGet(() -> scrumRepository.save(Scrum.builder()
                                .worker(member)
                                .startAt(now)
                        .build())
                );
    }
}
