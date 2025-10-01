package com.example.backend.auth.oauth;

import com.example.backend.user.UserRepository;
import com.example.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String imageUrl = (String) attributes.get("picture"); // 프로필 이미지 URL 가져오기
        String providerId = oAuth2User.getName();

        User user = saveOrUpdate(email, name, imageUrl, provider, providerId);

        return new CustomOAuth2User(user);
    }

    private User saveOrUpdate(String email, String name, String imageUrl, String provider, String providerId) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            user.setProvider(provider);
            user.setProviderId(providerId);
            user.setImageUrl(imageUrl); // 기존 회원이면 프로필 이미지 업데이트
        } else {
            user = new User();
            user.setEmail(email);
            user.setUserName(name);
            user.setImageUrl(imageUrl); // 신규 회원이면 프로필 이미지 설정
            user.setProvider(provider);
            user.setProviderId(providerId);
            user.setEnabled(true);
        }
        return userRepository.save(user);
    }
}
