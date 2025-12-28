package com.nova.yeobaek.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
}
