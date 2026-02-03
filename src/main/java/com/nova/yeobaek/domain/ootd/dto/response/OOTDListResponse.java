package com.nova.yeobaek.domain.ootd.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OOTDListResponse {

    private List<OOTDListItemResponse> items;
    private Long nextCursor;
    private String nextCursorName;
    private boolean hasNext;
}