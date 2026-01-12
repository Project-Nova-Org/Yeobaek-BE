package com.nova.yeobaek.domain.ootd.service;

import com.nova.yeobaek.domain.item.domain.Item;
import com.nova.yeobaek.domain.item.repository.ItemRepository;
import com.nova.yeobaek.domain.ootd.converter.OOTDConverter;
import com.nova.yeobaek.domain.ootd.domain.OOTD;
import com.nova.yeobaek.domain.ootd.domain.Style;
import com.nova.yeobaek.domain.ootd.domain.TPO;
import com.nova.yeobaek.domain.ootd.domain.mapping.OOTDItem;
import com.nova.yeobaek.domain.ootd.dto.request.OOTDRequestDTO;
import com.nova.yeobaek.domain.ootd.repository.OOTDRepository;
import com.nova.yeobaek.domain.ootd.repository.ootdItem.OOTDItemRepository;
import com.nova.yeobaek.domain.ootd.repository.style.StyleRepository;
import com.nova.yeobaek.domain.ootd.repository.tpo.TPORepository;
import com.nova.yeobaek.domain.shared.ImageBackgroundColor;
import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.service.UserService;
import com.nova.yeobaek.global.payload.exception.GeneralException;
import com.nova.yeobaek.global.payload.status.CommonErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OOTDService {

    private final OOTDRepository ootdRepository;
    private final OOTDItemRepository ootdItemRepository;
    private final StyleRepository styleRepository;
    private final TPORepository tpoRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    /** OOTD 등록 */
    @Transactional
    public Long createOOTD(OOTDRequestDTO requestDTO) {

        User user = null; // TODO: 인증 연동 시 수정

        Style style = styleRepository.findById(requestDTO.getStyleId())
                .orElseThrow(() -> new GeneralException(CommonErrorStatus._BAD_REQUEST));

        TPO tpo = tpoRepository.findById(requestDTO.getTpoId())
                .orElseThrow(() -> new GeneralException(CommonErrorStatus._BAD_REQUEST));

        // ImageBackgroundColor 검증
        ImageBackgroundColor backgroundColor;
        try {
            backgroundColor = ImageBackgroundColor.from(requestDTO.getImageBackground());
        } catch (IllegalArgumentException e) {
            throw new GeneralException(CommonErrorStatus._BAD_REQUEST);
        }

        // 요청된 Item ID 목록 추출
        List<Long> itemIds = requestDTO.getItems().stream()
                .map(OOTDRequestDTO.OOTDItemRequestDTO::getFashionItemId)
                .toList();

        // Item 엔티티 조회 (영속 상태)
        List<Item> items = itemRepository.findAllById(itemIds);

        // 존재 여부 검증
        if (items.size() != itemIds.size()) {
            throw new GeneralException(CommonErrorStatus._BAD_REQUEST);
        }

        // Map 변환 (itemId -> Item)
        Map<Long, Item> itemMap = items.stream()
                .collect(Collectors.toMap(Item::getId, item -> item));

        // OOTD 생성
        OOTD ootd = OOTDConverter.toOOTD(
                requestDTO,
                user,
                style,
                tpo,
                backgroundColor
        );
        ootdRepository.save(ootd);

        // OOTDItem 생성 (영속 상태 Item 사용)
        List<OOTDItem> ootdItems =
                OOTDConverter.toOOTDItems(
                        requestDTO.getItems(),
                        ootd,
                        itemMap
                );
        ootdItemRepository.saveAll(ootdItems);

        return ootd.getId();
    }
}