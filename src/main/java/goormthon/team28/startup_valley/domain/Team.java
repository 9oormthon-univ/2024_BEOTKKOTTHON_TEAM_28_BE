package goormthon.team28.startup_valley.domain;

import goormthon.team28.startup_valley.dto.type.EProjectStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "teams")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "guild_id", nullable = false, unique = true)
    private String guildId;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "team_image")
    private String teamImage;
    @OneToOne
    @JoinColumn(name = "leader_id")
    private Member leader;
    @Column(name = "start_at", nullable = false)
    private LocalDate startAt;
    @Column(name = "end_at")
    private LocalDate endAt;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EProjectStatus status;
    @Builder
    public Team(String guildId, String name, String teamImage, LocalDate startAt, EProjectStatus status) {
        this.guildId = guildId;
        this.name = name;
        this.teamImage = teamImage;
        this.startAt = startAt;
        this.status = status;
    }

    public void changeTeamLeader(Member leader) {
        this.leader = leader;
    }
}
