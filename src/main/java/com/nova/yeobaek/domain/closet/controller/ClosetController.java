package com.nova.yeobaek.domain.closet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.validation.annotation.Validated;

import com.nova.yeobaek.domain.closet.dto.request.ClosetEditRequestDTO;
import com.nova.yeobaek.domain.closet.dto.response.ClosetEditResponseDTO;

import com.nova.yeobaek.domain.closet.controller.docs.ClosetControllerDocs;
import com.nova.yeobaek.domain.closet.dto.request.ClosetItemQuery;
import com.nova.yeobaek.domain.closet.dto.request.ClosetRequestDTO;
import com.nova.yeobaek.domain.closet.dto.response.ClosetResponseDTO;
import com.nova.yeobaek.domain.closet.dto.type.ClosetSortType;
import com.nova.yeobaek.domain.closet.service.ClosetService;
import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.global.auth.security.CustomUserDetails;
import com.nova.yeobaek.global.payload.response.CommonResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/closets")
public class ClosetController implements ClosetControllerDocs {

	private final ClosetService closetService;

	@Override
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CommonResponse<ClosetResponseDTO.Create> createCloset(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@Valid @RequestBody ClosetRequestDTO.Create request
	) {
		User user = userDetails.getUser();
		Long closetId = closetService.create(user, request);
		return CommonResponse.onCreated(new ClosetResponseDTO.Create(closetId));
	}

	@Override
	@GetMapping
	public CommonResponse<ClosetResponseDTO.CursorListResponse> getClosets(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestParam(required = false) Long cursorId,
			@RequestParam(required = false) Boolean cursorFavorite,
			@Min(1) @Max(100) @RequestParam(defaultValue = "20") Integer size,
			@RequestParam(defaultValue = "LATEST") ClosetSortType sort
	) {
		User user = userDetails.getUser();
		return CommonResponse.onSuccess(
				closetService.listByCursor(user, cursorId, cursorFavorite, size, sort)
		);
	}

	@Override
	@GetMapping("/{closetId}")
	public CommonResponse<ClosetResponseDTO.Detail> getClosetDetail(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable Long closetId
	) {
		User user = userDetails.getUser();
		return CommonResponse.onSuccess(closetService.getDetail(user, closetId));
	}

	@Override
	@PatchMapping("/{closetId}/favorite")
	public CommonResponse<ClosetResponseDTO.FavoriteUpdate> updateFavorite(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable Long closetId,
			@Valid @RequestBody ClosetRequestDTO.FavoriteUpdate request
	) {
		User user = userDetails.getUser();
		return CommonResponse.onSuccess(
				closetService.updateFavorite(user, closetId, request.favorite())
		);
	}

	@Override
	@GetMapping("/{closetId}/items")
	public CommonResponse<ClosetResponseDTO.ClosetItemCursorListResponse> getClosetItems(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable Long closetId,
			@Valid @ModelAttribute ClosetItemQuery query
	) {
		User user = userDetails.getUser();
		return CommonResponse.onSuccess(
				closetService.getClosetItemsByCursor(
						user,
						closetId,
						query.cursorId(),
						query.sizeOrDefault(),
						query.level1CategoryId(),
						query.level2CategoryId()
				)
		);
	}

	@Override
	@GetMapping("/{closetId}/edit")
	public CommonResponse<ClosetEditResponseDTO.EditInfo> getClosetEditInfo(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable Long closetId
	) {
		User user = userDetails.getUser();
		return CommonResponse.onSuccess(closetService.getClosetEditInfo(user, closetId));
	}

	@Override
	@GetMapping("/{closetId}/edit/items")
	public CommonResponse<ClosetEditResponseDTO.EditableItemCursorList> getEditableItems(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable Long closetId,
			@Valid @ModelAttribute ClosetItemQuery query
	) {
		User user = userDetails.getUser();
		return CommonResponse.onSuccess(
				closetService.getEditableItemsByCursor(
						user,
						closetId,
						query.cursorId(),
						query.sizeOrDefault(),
						query.level1CategoryId(),
						query.level2CategoryId()
				)
		);
	}

	@Override
	@PatchMapping("/{closetId}")
	public CommonResponse<ClosetEditResponseDTO.UpdateResult> updateCloset(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable Long closetId,
			@Valid @RequestBody ClosetEditRequestDTO.Update request
	) {
		User user = userDetails.getUser();
		Long updatedClosetId = closetService.updateCloset(user, closetId, request);
		return CommonResponse.onSuccess(new ClosetEditResponseDTO.UpdateResult(updatedClosetId));
	}
}
