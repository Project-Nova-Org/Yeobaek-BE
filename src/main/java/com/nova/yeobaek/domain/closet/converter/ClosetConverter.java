package com.nova.yeobaek.domain.closet.converter;

import org.springframework.stereotype.Component;

import com.nova.yeobaek.domain.closet.domain.Closet;
import com.nova.yeobaek.domain.closet.dto.request.ClosetRequestDTO;
import com.nova.yeobaek.domain.closet.dto.response.ClosetResponseDTO;
import com.nova.yeobaek.domain.user.domain.User;

@Component
public class ClosetConverter {

    public Closet toEntity(User user, ClosetRequestDTO.Create request) {
        return Closet.create(user, request.name(), request.imageUrl());
    }

    public ClosetResponseDTO.Create toCreateResponse(Closet closet) {
        return new ClosetResponseDTO.Create(closet.getId());
    }
}
