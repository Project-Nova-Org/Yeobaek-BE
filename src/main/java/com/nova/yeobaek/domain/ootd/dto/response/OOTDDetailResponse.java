package com.nova.yeobaek.domain.ootd.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OOTDDetailResponse {

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