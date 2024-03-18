package goormthon.team28.startup_valley.domain;

import goormthon.team28.startup_valley.dto.type.EScrumStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Entity
@Getter
@DynamicUpdate
@Table(name = "scrums")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scrum {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "summary")
    private String summary;
    @Column(name = "start_at", nullable = false)
    private LocalDate startAt;
    @Column(name = "end_at")
    private LocalDate endAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id")
    private Member worker;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EScrumStatus status;


}
