package com.nova.yeobaek.domain.item.repository;

import com.nova.yeobaek.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.item.domain.Item;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {

    Optional<Item> findByIdAndUser(Long id, User user);
}
