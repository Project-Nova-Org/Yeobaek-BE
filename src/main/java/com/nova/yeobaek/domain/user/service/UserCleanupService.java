package com.nova.yeobaek.domain.user.service;

import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.domain.enums.UserStatus;
import com.nova.yeobaek.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCleanupService {
    private final UserRepository userRepository;

    @Transactional
    public void hardDeleteExpiredUsers() {
        LocalDateTime deletedAt = LocalDateTime.now().minusDays(30);

        List<User> expiredUsers =
                userRepository.findAllByStatusAndInactiveDateBefore(
                        UserStatus.DELETED,
                        deletedAt
                );

        if (expiredUsers.isEmpty()) {
            return;
        }

        List<Long> ids = expiredUsers.stream()
                .map(User::getId)
                .toList();

        userRepository.deleteAllByIdIn(ids);

        log.info("30일 지난 탈퇴 유저 {}명 hard delete 완료", ids.size());
    }
}
