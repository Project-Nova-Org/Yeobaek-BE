package com.nova.yeobaek.domain.ootd.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OOTDListItemResponse {

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