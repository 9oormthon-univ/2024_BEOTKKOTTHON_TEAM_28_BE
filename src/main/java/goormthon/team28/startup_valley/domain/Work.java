package goormthon.team28.startup_valley.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Getter
@DynamicUpdate
@Table(name = "works")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Work {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scrum_id", nullable = false)
    private Scrum scrum;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member owner;
    @Column(name = "content")
    private String content;
    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;
    @Column(name = "end_at")
    private LocalDateTime endAt;
  
    @Builder
    public Work(Scrum scrum, Member owner, LocalDateTime startAt) {
        this.scrum = scrum;
        this.owner = owner;
        this.startAt = startAt;
    }

    public void updateTime(LocalDateTime startAt, LocalDateTime endAt) {
        this.startAt = startAt;
        this.endAt = endAt;
    }
}
