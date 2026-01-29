package com.nova.yeobaek.domain.user.repository;

import com.nova.yeobaek.domain.user.domain.enums.OauthProvider;
import com.nova.yeobaek.domain.user.domain.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.user.domain.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    Optional<User> findByOauthProviderAndOauthId(
            OauthProvider oauthProvider,
            String oauthId
    );

    boolean existsByNickname(String nickname);

    Optional<User> findByIdAndStatus(Long id, UserStatus status);

    List<User> findAllByStatusAndInactiveDateBefore(
            UserStatus status,
            LocalDateTime deletedAt
    );

    void deleteAllByIdIn(List<Long> ids);}
