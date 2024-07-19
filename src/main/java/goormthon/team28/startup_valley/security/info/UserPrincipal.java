package goormthon.team28.startup_valley.security.info;

import goormthon.team28.startup_valley.dto.type.ERole;
import goormthon.team28.startup_valley.repository.UserRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
@Builder
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails, OAuth2User {
    private final Long userId;
    private final String password;
    private final ERole role;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal create(UserRepository.UserSecurityForm securityForm){
        return UserPrincipal.builder()
                .userId(securityForm.getId())
                .password(securityForm.getPassword())
                .role(securityForm.getRole())
                .authorities(Collections.singleton(new SimpleGrantedAuthority(securityForm.getRole().getSecurityRole())))
                .build();
    }

    public static UserPrincipal create(UserRepository.UserSecurityForm securityForm, Map<String, Object> attributes) {
        return UserPrincipal.builder()
                .userId(securityForm.getId())
                .password(securityForm.getPassword())
                .role(securityForm.getRole())
                .attributes(attributes)
                .authorities(Collections.singleton(new SimpleGrantedAuthority(securityForm.getRole().getSecurityRole())))
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userId.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return userId.toString();
    }
}
