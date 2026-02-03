package com.nova.yeobaek.domain.closet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nova.yeobaek.domain.closet.domain.Closet;
import com.nova.yeobaek.domain.user.domain.User;

public interface ClosetRepository extends JpaRepository<Closet, Long> {


    boolean existsByUserAndName(User user, String name);
}
