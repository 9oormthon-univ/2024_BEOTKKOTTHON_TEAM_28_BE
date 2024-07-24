package goormthon.team28.startup_valley.domain;

import goormthon.team28.startup_valley.dto.type.EProfileImage;
import goormthon.team28.startup_valley.dto.type.EProvider;
import goormthon.team28.startup_valley.dto.type.ERole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@DynamicUpdate
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /*  사용자 인증 및 권한 정보   */
    @Column(name = "serial_id", nullable = false, unique = true)
    private String serialId; // discord serial id
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "discord_id")
    private Long discordId;
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private ERole role;
    @Column(name = "provider", nullable = false)
    @Enumerated(EnumType.STRING)
    private EProvider provider;

    /*  사용자 이용 정보  */
    @Column(name = "nickname", nullable = false)
    private String nickname;
    @Column(name = "profile_image", nullable = false)
    @Enumerated(EnumType.STRING)
    private EProfileImage profileImage;
    @Column(name = "refresh_Token")
    private String refreshToken;
  
    @Builder
    public User(
            String serialId,
            String password,
            ERole role,
            EProfileImage profileImage,
            EProvider provider
    ) {
        this.serialId = serialId;
        this.password = password;
        this.role = role;
        this.profileImage = profileImage;
        this.provider = provider;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateUserInfo(String nickname, EProfileImage eProfileImage) {
        this.nickname = nickname;
        this.profileImage = eProfileImage;
    }
}
