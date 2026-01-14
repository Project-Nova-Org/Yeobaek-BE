package com.nova.yeobaek.domain.closet.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nova.yeobaek.domain.closet.domain.Closet;
import com.nova.yeobaek.domain.closet.dto.request.ClosetRequestDTO;
import com.nova.yeobaek.domain.closet.repository.ClosetRepository;
import com.nova.yeobaek.domain.closet.status.ClosetErrorStatus;
import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.global.payload.exception.GeneralException;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClosetService {

    private final ClosetRepository closetRepository;

    public Long create(User user, ClosetRequestDTO.Create request) {

        // ✅ 이름 특수문자 검증만 유지
        if (!request.name().matches("^[a-zA-Z0-9가-힣\\s]+$")) {
            throw new GeneralException(ClosetErrorStatus.INVALID_NAME);
        }

        Closet closet = Closet.create(user, request.name(), request.imageUrl());
        return closetRepository.save(closet).getId();
    }
}
