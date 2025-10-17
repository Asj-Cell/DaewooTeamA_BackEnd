package com.example.backend.auth.oauth;

import com.example.backend.user.UserRepository;
import com.example.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email;
        String name;
        String imageUrl;
        String providerId;

        switch (provider) {
            case "google":
                email = (String) attributes.get("email");
                name = (String) attributes.get("name");
                imageUrl = (String) attributes.get("picture");
                providerId = oAuth2User.getName();
                break;
            case "naver":
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                email = (String) response.get("email");
                name = (String) response.get("name");
                imageUrl = (String) response.get("profile_image");
                providerId = (String) response.get("id");
                break;
            default:
                throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다.");
        }

        // ⬇️⬇️⬇️ 이름(name)이 null일 경우, 이메일에서 @ 앞부분을 이름으로 사용하도록 수정 ⬇️⬇️⬇️
        if (!StringUtils.hasText(name) && StringUtils.hasText(email)) {
            name = email.split("@")[0];
        }

        User user = saveOrUpdate(email, name, imageUrl, provider, providerId);

        return new CustomOAuth2User(user);
    }

    private User saveOrUpdate(String email, String name, String imageUrl, String provider, String providerId) {
        User user = userRepository.findByEmail(email)
                .map(entity -> {
                    entity.setUserName(name);
                    entity.setImageUrl(imageUrl);
                    entity.setProvider(provider);
                    entity.setProviderId(providerId);
                    return entity;
                })
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUserName(name);
                    newUser.setImageUrl(imageUrl);
                    newUser.setProvider(provider);
                    newUser.setProviderId(providerId);
                    newUser.setEnabled(true);
                    return newUser;
                });

        return userRepository.save(user);
    }
}
