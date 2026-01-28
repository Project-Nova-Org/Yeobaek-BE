package com.nova.yeobaek.domain.user.service;

import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.domain.enums.UserStatus;
import com.nova.yeobaek.domain.user.dto.request.UserRequestDTO;
import com.nova.yeobaek.domain.user.dto.response.UserResponseDTO;
import com.nova.yeobaek.domain.user.exception.UserException;
import com.nova.yeobaek.domain.user.repository.UserRepository;
import com.nova.yeobaek.domain.user.status.UserErrorStatus;
import com.nova.yeobaek.global.auth.dto.request.RequestDTO;
import com.nova.yeobaek.global.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;

    // 닉네임 최초 설정
    public void createNickname(Long userId, RequestDTO.SignupRequest request) {
        User user = getUser(userId);

        if (user.getNickname() != null) {
            throw new UserException(UserErrorStatus.ALREADY_SIGNUP);
        }

        changeNickname(user, request.NewNickname());
    }

    // 닉네임 변경
    public void setNickname(Long userId, RequestDTO.SignupRequest request) {

        User user = getUser(userId);
        if (Objects.equals(user.getNickname(), request.NewNickname())) {
            throw new UserException(UserErrorStatus.SAME_NICK_NAME);
        }

        changeNickname(user, request.NewNickname());
    }

    // 닉네임 변경 로직
    private void changeNickname(User user, String nickname) {
        try {
            user.updateNickname(nickname);
        } catch (DataIntegrityViolationException e) {
            throw new UserException(UserErrorStatus.DUPLICATE_NICKNAME);
        }
    }

    // 유저 조회
    private User getUser(Long userId) {
        return userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() ->
                        new UserException(UserErrorStatus.USER_NOT_FOUND)
                );
    }

    public UserResponseDTO.PreferenceResponse setPreference(Long userId, UserRequestDTO.PreferenceRequest request) {

        User user = getUser(userId);

        try {
            user.updatePreference(
                    request.height(),
                    request.weight(),
                    request.gender(),
                    request.bodyImageUrl()
            );
        } catch (DataIntegrityViolationException e) {
            throw new UserException(UserErrorStatus.INVALID_PREFERENCE);
        }
        return UserResponseDTO.PreferenceResponse.from(user);
    }

    public UserResponseDTO.WithDrawResponse withdraw(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorStatus.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new UserException(UserErrorStatus.ALREADY_WITHDRAWN);
        }

        user.withdraw();
        authService.logoutAll(userId);
        return UserResponseDTO.WithDrawResponse.from(user);
    }

    public UserResponseDTO.GetMyPageResponse getMypage(Long userId){

        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() ->
                        new UserException(UserErrorStatus.USER_NOT_FOUND)
                );

        return UserResponseDTO.GetMyPageResponse.from(user);
    }
}
