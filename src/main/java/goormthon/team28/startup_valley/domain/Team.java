package goormthon.team28.startup_valley.domain;

import goormthon.team28.startup_valley.dto.type.EProjectStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.time.LocalDate;

@Entity
@Getter
@DynamicUpdate
@Table(name = "teams")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "team_image")
    private String teamImage;
    @OneToOne
    @JoinColumn(name = "id", nullable = false)
    private Member leader;
    @Column(name = "start_at", nullable = false)
    private LocalDate startAt;
    @Column(name = "end_at")
    private LocalDate endAt;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EProjectStatus status;
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;
}
