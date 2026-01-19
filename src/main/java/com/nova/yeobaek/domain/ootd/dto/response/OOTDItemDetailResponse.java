package com.nova.yeobaek.domain.ootd.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OOTDItemDetailResponse {

    private Long fashionItemId;
    private float posX;
    private float posY;
    private float scale;
    private float rotation;
    private int zIndex;
}