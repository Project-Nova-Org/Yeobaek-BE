package com.nova.yeobaek.domain.closet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.nova.yeobaek.domain.closet.controller.docs.ClosetControllerDocs;
import com.nova.yeobaek.domain.closet.dto.request.ClosetRequestDTO;
import com.nova.yeobaek.domain.closet.dto.type.ClosetSortType;
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
	@ResponseStatus(HttpStatus.CREATED)
	public CommonResponse<ClosetResponseDTO.Create> createCloset(
			@AuthenticationPrincipal(expression = "user") User user,
			@Valid @RequestBody ClosetRequestDTO.Create request
	) {
		Long closetId = closetService.create(user, request);
		return CommonResponse.onCreated(new ClosetResponseDTO.Create(closetId));
	}

	@Override
	@GetMapping
	public CommonResponse<ClosetResponseDTO.CursorListResponse> getClosets(
			@AuthenticationPrincipal(expression = "user") User user,
			@RequestParam(required = false) Long cursorId,
			@RequestParam(required = false) Boolean cursorFavorite,
			@RequestParam(defaultValue = "20") Integer size,
			@RequestParam(defaultValue = "LATEST") ClosetSortType sort
	) {
		return CommonResponse.onSuccess(closetService.listByCursor(user, cursorId, cursorFavorite, size, sort));
	}

	@Override
	@GetMapping("/{closetId}")
	public CommonResponse<ClosetResponseDTO.Detail> getClosetDetail(
			@AuthenticationPrincipal(expression = "user") User user,
			@PathVariable Long closetId
	) {
		return CommonResponse.onSuccess(closetService.getDetail(user, closetId));
	}

	@Override
	@PatchMapping("/{closetId}/favorite")
	public CommonResponse<ClosetResponseDTO.FavoriteUpdate> updateFavorite(
			@AuthenticationPrincipal(expression = "user") User user,
			@PathVariable Long closetId,
			@Valid @RequestBody ClosetRequestDTO.FavoriteUpdate request
	) {
		return CommonResponse.onSuccess(closetService.updateFavorite(user, closetId, request.favorite()));
	}

	@GetMapping("/{closetId}/items")
	public CommonResponse<ClosetResponseDTO.ClosetItemCursorListResponse> getClosetItems(
			@AuthenticationPrincipal(expression = "user") User user,
			@PathVariable Long closetId,
			@RequestParam(required = false) Long cursorId,
			@RequestParam(defaultValue = "20") Integer size,
			@RequestParam(required = false) Long level1CategoryId,
			@RequestParam(required = false) Long level2CategoryId
	) {
		return CommonResponse.onSuccess(
				closetService.getClosetItemsByCursor(
						user,
						closetId,
						cursorId,
						size,
						level1CategoryId,
						level2CategoryId
				)
		);
	}
}
