package com.nova.yeobaek.domain.calendar.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nova.yeobaek.domain.calendar.controller.docs.CalendarControllerDocs;
import com.nova.yeobaek.domain.calendar.dto.request.CalendarRequestDTO;
import com.nova.yeobaek.domain.calendar.dto.response.CalendarResponseDTO;
import com.nova.yeobaek.domain.calendar.service.CalendarService;
import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.global.payload.response.CommonResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/calendar") // ✅ /api/calendar/entries/{date}
@Validated
public class CalendarController implements CalendarControllerDocs {

	private final CalendarService calendarService;

	@Override
	@GetMapping("/entries/{date}")
	public CommonResponse<CalendarResponseDTO.EntryDetailResponse> getEntryDetail(
			@AuthenticationPrincipal(expression = "user") User user,
			@PathVariable String date
	) {
		return CommonResponse.onSuccess(calendarService.getEntryDetail(user.getId(), date));
	}

	@Override
	@PostMapping("/entries/{date}")
	public CommonResponse<CalendarResponseDTO.EntryDetailResponse> connectOotd(
			@AuthenticationPrincipal(expression = "user") User user,
			@PathVariable String date,
			@RequestBody @Valid CalendarRequestDTO.ConnectOotdRequest request
	) {
		return CommonResponse.onSuccess(calendarService.connectOotd(user.getId(), date, request.getOotdId()));
	}

	@Override
	@DeleteMapping("/entries/{date}")
	public CommonResponse<CalendarResponseDTO.EntryDeleteResponse> disconnectOotd(
			@AuthenticationPrincipal(expression = "user") User user,
			@PathVariable String date
	) {
		return CommonResponse.onSuccess(calendarService.disconnectOotd(user.getId(), date));
	}
}
