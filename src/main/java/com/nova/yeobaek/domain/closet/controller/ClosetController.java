package com.nova.yeobaek.domain.closet.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.nova.yeobaek.domain.closet.controller.docs.ClosetControllerDocs;
import com.nova.yeobaek.domain.closet.dto.request.ClosetRequestDTO;
import com.nova.yeobaek.domain.closet.dto.response.ClosetResponseDTO;
import com.nova.yeobaek.domain.closet.service.ClosetService;
import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.global.payload.response.CommonResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/closets")
public class ClosetController implements ClosetControllerDocs {

	private final ClosetService closetService;

	@Override
	@PostMapping
	public CommonResponse<ClosetResponseDTO.Create> createCloset(
			@AuthenticationPrincipal(expression = "user") User user,
			@Valid @RequestBody ClosetRequestDTO.Create request
	) {
		Long closetId = closetService.create(user, request);
		return CommonResponse.onCreated(new ClosetResponseDTO.Create(closetId));
	}
}
