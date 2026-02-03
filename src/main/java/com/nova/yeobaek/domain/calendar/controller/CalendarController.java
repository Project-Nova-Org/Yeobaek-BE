package com.nova.yeobaek.domain.calendar.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequestMapping("/api/calendars")
@Validated
public class CalendarController implements CalendarControllerDocs {

	private final CalendarService calendarService;

	// =========================
	// 날짜 단위 API
	// =========================

	@Override
	@GetMapping("/entries/{date}")
	public CommonResponse<CalendarResponseDTO.EntryDetailResponse> getEntryDetail(
			@AuthenticationPrincipal(expression = "user") User user,
			@PathVariable("date") String date
	) {
		return CommonResponse.onSuccess(calendarService.getEntryDetail(user.getId(), date));
	}

	@Override
	@PostMapping("/entries/{date}")
	public CommonResponse<CalendarResponseDTO.EntryDetailResponse> createEntry(
			@AuthenticationPrincipal(expression = "user") User user,
			@PathVariable("date") String date,
			@RequestBody @Valid CalendarRequestDTO.CreateEntryRequest request
	) {
		return CommonResponse.onSuccess(calendarService.createEntry(user.getId(), date, request.ootdId()));
	}

	@Override
	@DeleteMapping("/entries/{date}")
	public CommonResponse<CalendarResponseDTO.EntryDeleteResponse> deleteEntry(
			@AuthenticationPrincipal(expression = "user") User user,
			@PathVariable("date") String date
	) {
		return CommonResponse.onSuccess(calendarService.deleteEntry(user.getId(), date));
	}

	@Override
	@PostMapping("/entries/{date}/custom-image")
	public CommonResponse<CalendarResponseDTO.EntryDetailResponse> addCustomImage(
			@AuthenticationPrincipal(expression = "user") User user,
			@PathVariable("date") String date,
			@RequestBody @Valid CalendarRequestDTO.CustomImageRequest request
	) {
		return CommonResponse.onSuccess(calendarService.addCustomImage(user.getId(), date, request.imageUrl()));
	}

	@Override
	@DeleteMapping("/entries/{date}/custom-image")
	public CommonResponse<CalendarResponseDTO.EntryDetailResponse> deleteCustomImage(
			@AuthenticationPrincipal(expression = "user") User user,
			@PathVariable("date") String date
	) {
		return CommonResponse.onSuccess(calendarService.deleteCustomImage(user.getId(), date));
	}

	@Override
	@PatchMapping("/entries/{date}/thumbnail")
	public CommonResponse<CalendarResponseDTO.EntryDetailResponse> updateThumbnail(
			@AuthenticationPrincipal(expression = "user") User user,
			@PathVariable("date") String date,
			@RequestBody @Valid CalendarRequestDTO.UpdateThumbnailRequest request
	) {
		return CommonResponse.onSuccess(calendarService.updateThumbnail(user.getId(), date, request.thumbnail()));
	}

	// =========================
	//  월 캘린더 API (Docs 구현 방식으로 통일)
	// =========================

	@Override
	@GetMapping("/months/{yearMonth}")
	public CommonResponse<CalendarResponseDTO.MonthlyCalendarResponse> getMonthlyCalendar(
			@AuthenticationPrincipal(expression = "user") User user,
			@PathVariable("yearMonth") String yearMonth
	) {
		return CommonResponse.onSuccess(calendarService.getMonthlyCalendar(user.getId(), yearMonth));
	}

	@Override
	@GetMapping("/months/{yearMonth}/image")
	public CommonResponse<CalendarResponseDTO.MonthImageResponse> getMonthImage(
			@AuthenticationPrincipal(expression = "user") User user,
			@PathVariable("yearMonth") String yearMonth
	) {
		return CommonResponse.onSuccess(calendarService.getMonthImage(user.getId(), yearMonth));
	}

	@Override
	@PostMapping("/months/{yearMonth}/image")
	public CommonResponse<CalendarResponseDTO.MonthImageSaveResponse> saveMonthImage(
			@AuthenticationPrincipal(expression = "user") User user,
			@PathVariable("yearMonth") String yearMonth,
			@RequestBody @Valid CalendarRequestDTO.SaveMonthImageRequest request
	) {
		return CommonResponse.onCreated(
				calendarService.saveMonthImage(user.getId(), yearMonth, request.monthlyOotdImageUrl())
		);
	}
}
