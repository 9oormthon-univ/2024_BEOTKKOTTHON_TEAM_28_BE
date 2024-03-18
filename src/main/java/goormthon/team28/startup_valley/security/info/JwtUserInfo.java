package goormthon.team28.startup_valley.security.info;

import goormthon.team28.startup_valley.dto.type.ERole;

public record JwtUserInfo(Long userId, ERole role) {
}
