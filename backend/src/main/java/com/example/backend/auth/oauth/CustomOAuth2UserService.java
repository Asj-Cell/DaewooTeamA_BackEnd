package com.example.backend.auth.oauth;

import com.example.backend.user.UserRepository;
import com.example.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            log.info("=== OAuth2 로그인 시작 ===");

            // 1. 부모 클래스로 OAuth2 사용자 정보 가져오기
            OAuth2User oAuth2User = super.loadUser(userRequest);

            // 2. 제공자(google/naver) 확인
            String provider = userRequest.getClientRegistration().getRegistrationId();
            log.info("OAuth2 Provider: {}", provider);

            // 3. Attributes null 체크
            Map<String, Object> attributes = oAuth2User.getAttributes();
            if (attributes == null || attributes.isEmpty()) {
                log.error("OAuth2 attributes가 비어있습니다. Provider: {}", provider);
                throw new OAuth2AuthenticationException(
                        new OAuth2Error("invalid_user_info", "사용자 정보를 가져올 수 없습니다.", null)
                );
            }

            log.debug("OAuth2 Attributes: {}", attributes);

            // 4. 제공자별로 사용자 정보 추출
            String email;
            String name;
            String imageUrl;
            String providerId;

            try {
                switch (provider.toLowerCase()) {
                    case "google":
                        email = extractGoogleEmail(attributes);
                        name = extractGoogleName(attributes);
                        imageUrl = extractGoogleImage(attributes);
                        providerId = oAuth2User.getName();
                        log.info("Google 사용자 정보 추출 완료 - email: {}, name: {}", email, name);
                        break;

                    case "naver":
                        email = extractNaverEmail(attributes);
                        name = extractNaverName(attributes);
                        imageUrl = extractNaverImage(attributes);
                        providerId = extractNaverProviderId(attributes);
                        log.info("Naver 사용자 정보 추출 완료 - email: {}, name: {}", email, name);
                        break;

                    default:
                        log.error("지원하지 않는 OAuth2 Provider: {}", provider);
                        throw new OAuth2AuthenticationException(
                                new OAuth2Error("unsupported_provider",
                                        "지원하지 않는 로그인 방식입니다: " + provider, null)
                        );
                }

                // 5. 필수 정보 검증
                validateUserInfo(email, name, providerId, provider);

                // 6. DB에 저장 또는 업데이트
                User user = saveOrUpdate(email, name, imageUrl, provider, providerId);
                log.info("사용자 DB 저장/업데이트 완료 - userId: {}, email: {}", user.getId(), user.getEmail());

                // 7. CustomOAuth2User 반환
                return new CustomOAuth2User(user);

            } catch (ClassCastException | NullPointerException e) {
                log.error("사용자 정보 추출 중 오류 발생 - Provider: {}, Error: {}", provider, e.getMessage(), e);
                throw new OAuth2AuthenticationException(
                        new OAuth2Error("invalid_user_info",
                                "사용자 정보 형식이 올바르지 않습니다.", null), e
                );
            }

        } catch (OAuth2AuthenticationException e) {
            log.error("OAuth2 인증 오류: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 예상치 못한 오류 발생", e);
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("oauth2_error", "로그인 처리 중 오류가 발생했습니다.", null), e
            );
        }
    }

    // Google 정보 추출 메서드들
    private String extractGoogleEmail(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        if (email == null || email.trim().isEmpty()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("missing_email", "Google 이메일 정보를 찾을 수 없습니다.", null)
            );
        }
        return email;
    }

    private String extractGoogleName(Map<String, Object> attributes) {
        String name = (String) attributes.get("name");
        return (name != null && !name.trim().isEmpty()) ? name : "Google User";
    }

    private String extractGoogleImage(Map<String, Object> attributes) {
        return (String) attributes.get("picture");
    }

    // Naver 정보 추출 메서드들
    private String extractNaverEmail(Map<String, Object> attributes) {
        Map<String, Object> response = getNaverResponse(attributes);
        String email = (String) response.get("email");
        if (email == null || email.trim().isEmpty()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("missing_email", "Naver 이메일 정보를 찾을 수 없습니다.", null)
            );
        }
        return email;
    }

    private String extractNaverName(Map<String, Object> attributes) {
        Map<String, Object> response = getNaverResponse(attributes);
        String name = (String) response.get("name");
        return (name != null && !name.trim().isEmpty()) ? name : "Naver User";
    }

    private String extractNaverImage(Map<String, Object> attributes) {
        Map<String, Object> response = getNaverResponse(attributes);
        return (String) response.get("profile_image");
    }

    private String extractNaverProviderId(Map<String, Object> attributes) {
        Map<String, Object> response = getNaverResponse(attributes);
        String id = (String) response.get("id");
        if (id == null || id.trim().isEmpty()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("missing_provider_id", "Naver ID 정보를 찾을 수 없습니다.", null)
            );
        }
        return id;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getNaverResponse(Map<String, Object> attributes) {
        Object responseObj = attributes.get("response");
        if (responseObj == null) {
            log.error("Naver response 객체가 null입니다. Attributes: {}", attributes);
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_response",
                            "Naver 응답 형식이 올바르지 않습니다.", null)
            );
        }

        if (!(responseObj instanceof Map)) {
            log.error("Naver response가 Map 타입이 아닙니다. Type: {}", responseObj.getClass());
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_response",
                            "Naver 응답 형식이 올바르지 않습니다.", null)
            );
        }

        return (Map<String, Object>) responseObj;
    }

    // 필수 정보 검증
    private void validateUserInfo(String email, String name, String providerId, String provider) {
        if (email == null || email.trim().isEmpty()) {
            log.error("이메일이 비어있습니다. Provider: {}", provider);
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("missing_email", "이메일 정보가 필요합니다.", null)
            );
        }

        if (providerId == null || providerId.trim().isEmpty()) {
            log.error("Provider ID가 비어있습니다. Provider: {}", provider);
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("missing_provider_id", "사용자 식별 정보가 필요합니다.", null)
            );
        }
    }

    // DB 저장 또는 업데이트
    private User saveOrUpdate(String email, String name, String imageUrl,
                              String provider, String providerId) {
        try {
            Optional<User> existingUser = userRepository.findByEmail(email);

            if (existingUser.isPresent()) {
                // 기존 사용자 업데이트
                User user = existingUser.get();
                log.info("기존 사용자 업데이트 - userId: {}", user.getId());

                user.setUserName(name);
                user.setImageUrl(imageUrl);
                user.setProvider(provider);
                user.setProviderId(providerId);

                return userRepository.save(user);
            } else {
                // 신규 사용자 생성
                log.info("신규 사용자 생성 - email: {}", email);

                User newUser = User.builder()
                        .email(email)
                        .userName(name)
                        .imageUrl(imageUrl)
                        .provider(provider)
                        .providerId(providerId)
                        .enabled(true)
                        .build();

                return userRepository.save(newUser);
            }
        } catch (Exception e) {
            log.error("사용자 저장 중 데이터베이스 오류 발생 - email: {}", email, e);
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("database_error",
                            "사용자 정보 저장 중 오류가 발생했습니다.", null), e
            );
        }
    }
}
