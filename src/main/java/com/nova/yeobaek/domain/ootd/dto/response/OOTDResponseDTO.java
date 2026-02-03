package com.nova.yeobaek.domain.ootd.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class OOTDResponseDTO {

	public record CreateOOTDResponse(Long ootdId) {}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class OOTDDetailResponse {

		private Long ootdId;
		private String name;
		private String memo;
		private boolean favorite;
		private String imageBackground;
		private String imageUrl;
		private Long tpoId;
		private Long styleId;
		private LocalDateTime createdAt;

		private List<OOTDItemDetailResponse> items;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class OOTDItemDetailResponse {

		private Long fashionItemId;
		private float posX;
		private float posY;
		private float scale;
		private float rotation;
		private int zIndex;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class OOTDListItemResponse {

		private Long ootdId;
		private String name;
		private Long tpoId;
		private Long styleId;
		private boolean favorite;
		private String imageBackground;
		private String coverImageUrl;
		private int itemNum;
		private LocalDateTime createdAt;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class OOTDListResponse {

		private List<OOTDListItemResponse> items;
		private Long nextCursor;
		private String nextCursorName;
		private boolean hasNext;
	}
}
