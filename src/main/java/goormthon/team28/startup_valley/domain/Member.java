package goormthon.team28.startup_valley.domain;

import goormthon.team28.startup_valley.dto.type.EPart;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@DynamicUpdate
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
    @Column(name = "part")
    @Enumerated(EnumType.STRING)
    private EPart part;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "total_minute")
    private Long totalMinute;
    @Column(name = "retrospection")
    private String retrospection;
    @Builder
    public Member(Team team, User user) {
        this.team = team;
        this.user = user;
    }
}
