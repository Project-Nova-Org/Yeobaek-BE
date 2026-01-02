package com.nova.yeobaek.domain.user.service;

import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.exception.UserException;
import com.nova.yeobaek.domain.user.repository.UserRepository;
import com.nova.yeobaek.domain.user.status.UserErrorStatus;
import com.nova.yeobaek.global.auth.dto.request.RequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public void setNickname(Long userId, RequestDTO.SignupRequest request) {
        User user = getUser(userId);

        if (user.getNickname() != null) {
            throw new UserException(UserErrorStatus.ALREADY_SIGNUP);
        }

        validateNickname(user, request.nickname());
    }

    public void updateNickname(Long userId, RequestDTO.SignupRequest request) {
        User user = getUser(userId);
        validateNickname(user, request.nickname());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserException(UserErrorStatus.USER_NOT_FOUND)
                );
    }

    private void validateNickname(User user, String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new UserException(UserErrorStatus.DUPLICATE_NICKNAME);
        }
        user.updateNickname(nickname);
    }
}
