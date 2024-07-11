package goormthon.team28.startup_valley.security.info;

import goormthon.team28.startup_valley.security.info.factory.Oauth2UserInfo;

import java.util.Map;

public class DiscordOauth2UserInfo extends Oauth2UserInfo {
    public DiscordOauth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return this.attributes.get("id").toString();
    }
}
