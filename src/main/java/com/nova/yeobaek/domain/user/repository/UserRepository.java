package com.nova.yeobaek.domain.user.repository;

import com.nova.yeobaek.domain.user.domain.enums.OauthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.user.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    Optional<User> findByOauthProviderAndOauthId(
            OauthProvider oauthProvider,
            String oauthId
    );
    boolean existsByNickname(String nickname);
}
