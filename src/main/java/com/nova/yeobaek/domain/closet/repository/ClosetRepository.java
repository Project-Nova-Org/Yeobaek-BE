package com.nova.yeobaek.domain.closet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.closet.domain.Closet;
import com.nova.yeobaek.domain.user.domain.User;

public interface ClosetRepository extends JpaRepository<Closet, Long> {

    boolean existsByUserAndName(User user, String name);

    // === added ===
    List<Closet> findAllByUserOrderByIdDesc(User user);

    // === added ===
    Optional<Closet> findByIdAndUser(Long id, User user);
}
