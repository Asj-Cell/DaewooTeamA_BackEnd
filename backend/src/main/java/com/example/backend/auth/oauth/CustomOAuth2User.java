package com.example.backend.auth.oauth;

import com.example.backend.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {
    private final User user;

    public CustomOAuth2User(User user) {
        this.user = user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap(); // 사용하지 않으므로 비워둠
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 역할/권한 사용하지 않으므로 비워둠
    }

    @Override
    public String getName() {
        // Spring Security에서 사용자를 식별하는 key가 됨. 여기선 User ID를 반환
        return String.valueOf(user.getId());
    }
}