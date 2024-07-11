package goormthon.team28.startup_valley.security.info.factory;

import goormthon.team28.startup_valley.dto.type.EProvider;
import goormthon.team28.startup_valley.security.info.DiscordOauth2UserInfo;

import java.util.Map;

public class Oauth2UserInfoFactory {
    public static Oauth2UserInfo getOauth2UserInfo(
            EProvider provider,
            Map<String, Object> attributes
    ){
        Oauth2UserInfo ret;
        switch (provider) {
            case DISCORD -> ret =  new DiscordOauth2UserInfo(attributes);
            default -> throw new IllegalAccessError("잘못된 제공자 입니다.");
        }
        return ret;
    }
}
