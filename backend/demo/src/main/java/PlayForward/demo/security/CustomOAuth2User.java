package PlayForward.demo.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private Map<String, Object> attributes;
    private String nameAttributeKey;

    public CustomOAuth2User(Map<String, Object> attributes, String nameAttributeKey) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return attributes.get(nameAttributeKey).toString();
    }

    public String getEmail() {
        return (String) attributes.get("email");
    }

    public String getPicture() {
        return (String) attributes.get("picture");
    }
}
