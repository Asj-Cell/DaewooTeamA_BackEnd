package com.example.backend.user;

import com.example.backend.Reservation.Reservation;
import com.example.backend.Reservation.ReservationRepository;
import com.example.backend.common.util.FileStorageService;
import com.example.backend.pay.entity.Pay;
import com.example.backend.pay.PayRepository;
import com.example.backend.payment.PaymentRepository;
import com.example.backend.user.dto.*;
import com.example.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final FileStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder;
    private final ReservationRepository reservationRepository;
    private final PayRepository payRepository;

    @Transactional
    public void changeUserPassword(Long userId, ChangePasswordRequestDto requestDto) {
        // 1. 새 비밀번호와 확인용 비밀번호가 일치하는지 확인
        if (!requestDto.getNewPassword().equals(requestDto.getConfirmNewPassword())) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
        }

        // 2. 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 소셜 로그인 사용자인 경우 비밀번호 변경 불가
        if (user.getProvider() != null && !user.getProvider().isEmpty()) {
            throw new IllegalArgumentException("소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다.");
        }

        // 3. 현재 비밀번호가 맞는지 확인 (매우 중요!)
        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }

        // 4. 새 비밀번호를 암호화하여 저장
        user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public String updateUserProfileImage(Long userId, MultipartFile imageFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        // 파일을 서버에 저장하고 웹 경로를 받아옴
        String imageUrl = fileStorageService.storeFile(imageFile);

        // 사용자의 프로필 이미지 URL 업데이트
        user.setImageUrl(imageUrl);
        userRepository.save(user); // 변경 사항 저장

        return imageUrl; // 새로 업데이트된 이미지 URL 반환
    }

    @Transactional
    public String updateUserBackgroundImage(Long userId, MultipartFile imageFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        // 파일을 서버에 저장하고 웹 경로를 받아옴
        String backgroundImageUrl = fileStorageService.storeFile(imageFile);

        // 사용자의 배경 이미지 URL 업데이트
        user.setBackGroundImageUrl(backgroundImageUrl);
        userRepository.save(user); // 변경 사항 저장

        return backgroundImageUrl; // 새로 업데이트된 이미지 URL 반환
    }

    @Transactional
    public UserProfileAllResponseDto updateUserProfileInfo(Long userId, UserProfileRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        // DTO에 포함된 모든 필드를 업데이트하도록 로직 확장
        // 요청에 값이 있는 경우에만 업데이트하여 부분 수정이 가능하도록 함
        if (requestDto.getUserName() != null) {
            user.setUserName(requestDto.getUserName());
        }
        if (requestDto.getEmail() != null) {
            user.setEmail(requestDto.getEmail());
        }
        if (requestDto.getPhoneNumber() != null) {
            user.setPhoneNumber(requestDto.getPhoneNumber());
        }
        if (requestDto.getAddress() != null) {
            user.setAddress(requestDto.getAddress());
        }
        if (requestDto.getBirthDate() != null) {
            user.setBirthDate(requestDto.getBirthDate());
        }

        User updatedUser = userRepository.save(user);
        return new UserProfileAllResponseDto(updatedUser);
    }



    @Transactional(readOnly = true)
    public UserProfileAllResponseDto getUserProfileAll(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new UserProfileAllResponseDto(user);
    }
    @Transactional(readOnly = true)
    public UserProfileResponseDto getUserProfile(Long userId) {
        // ID로 사용자를 찾고, 없으면 예외를 발생시킵니다.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // User 엔티티를 UserProfileResponseDto로 변환하여 반환합니다.
        return new UserProfileResponseDto(user);
    }

    @Transactional(readOnly = true)
    public List<ReservationTicketDto> getUserReservations(Long userId) { // 1. 반환 타입 변경
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 2. Reservation을 ReservationTicketDto로 변환
        return user.getReservations().stream()
                .map(reservation -> ReservationTicketDto.builder()
                        .reservation(reservation)
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserProfilePaymentMethodDto> getUserPaymentMethods(Long userId) {
        // userId로 모든 Payment 엔티티를 조회
        return paymentRepository.findAllByUserId(userId).stream()
                .map(UserProfilePaymentMethodDto::new) // Payment -> DTO 변환
                .toList();
    }
    //유저 예약 삭제기능
    @Transactional
    public void cancelUserReservation(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약을 찾을 수 없습니다. ID: " + reservationId));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new SecurityException("예약을 취소할 권한이 없습니다.");
        }

        Pay pay = payRepository.findByReservation_Id(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("관련된 결제 내역을 찾을 수 없습니다."));

        payRepository.delete(pay);

        reservationRepository.delete(reservation);
    }

    @Transactional(readOnly = true)
    public ReservationTicketDto getReservationTicket(Long userId, Long reservationId) {
        // 1. 예약 정보 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약을 찾을 수 없습니다. ID: " + reservationId));

        // 2. 예약자와 로그인한 사용자가 일치하는지 권한 확인
        if (!reservation.getUser().getId().equals(userId)) {
            throw new SecurityException("예약 티켓을 조회할 권한이 없습니다.");
        }

        // 3. DTO 빌더를 사용하여 티켓 정보 생성 및 반환
        return ReservationTicketDto.builder()
                .reservation(reservation)
                .build();
    }
}