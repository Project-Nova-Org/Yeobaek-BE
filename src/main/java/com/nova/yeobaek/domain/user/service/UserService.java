package com.nova.yeobaek.domain.user.service;

import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.exception.UserException;
import com.nova.yeobaek.domain.user.repository.UserRepository;
import com.nova.yeobaek.domain.user.status.UserErrorStatus;
import com.nova.yeobaek.global.auth.dto.request.RequestDTO;
import org.springframework.dao.DataIntegrityViolationException;
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

    // 닉네임 최초설정
    public void setNickname(Long userId, RequestDTO.SignupRequest request) {
        User user = getUser(userId);

        // 첫 닉네임을 정한 유저는 넘김
        if (user.getNickname() != null) {
            throw new UserException(UserErrorStatus.ALREADY_SIGNUP);
        }

        validateNickname(request.nickname());

        try {
            user.updateNickname(request.nickname());
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserException(UserErrorStatus.DUPLICATE_NICKNAME);
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserException(UserErrorStatus.USER_NOT_FOUND)
                );
    }

    private void validateNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new UserException(UserErrorStatus.DUPLICATE_NICKNAME);
        }
    }
    //todo 닉네임 변경
}
