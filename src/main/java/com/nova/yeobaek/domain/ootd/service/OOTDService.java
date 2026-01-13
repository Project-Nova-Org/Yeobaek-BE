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
import com.nova.yeobaek.domain.ootd.status.OOTDErrorStatus;
import com.nova.yeobaek.domain.ootd.status.OOTDException;
import com.nova.yeobaek.domain.shared.ImageBackgroundColor;
import com.nova.yeobaek.domain.user.domain.User;

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
public class OOTDService {

    private final OOTDRepository ootdRepository;
    private final OOTDItemRepository ootdItemRepository;
    private final StyleRepository styleRepository;
    private final TPORepository tpoRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public Long createOOTD(User user, OOTDRequestDTO.Create requestDTO) {

        Style style = styleRepository.findById(requestDTO.styleId())
                .orElseThrow(() -> new OOTDException(OOTDErrorStatus.STYLE_NOT_FOUND));

        TPO tpo = tpoRepository.findById(requestDTO.tpoId())
                .orElseThrow(() -> new OOTDException(OOTDErrorStatus.TPO_NOT_FOUND));

        ImageBackgroundColor backgroundColor;
        try {
            backgroundColor = ImageBackgroundColor.from(requestDTO.imageBackground());
        } catch (IllegalArgumentException e) {
            throw new OOTDException(OOTDErrorStatus.INVALID_IMAGE_BACKGROUND);
        }

        List<Long> itemIds = requestDTO.items().stream()
                .map(OOTDRequestDTO.Item::fashionItemId)
                .toList();

        // 중복 ID 검증
        if (itemIds.size() != itemIds.stream().distinct().count()) {
            throw new OOTDException(OOTDErrorStatus.DUPLICATED_ITEM_ID);
        }

        // Item 조회
        List<Item> items = itemRepository.findAllById(itemIds);

        // 존재 여부 검증
        if (items.size() != itemIds.size()) {
            throw new OOTDException(OOTDErrorStatus.ITEM_NOT_FOUND);
        }

        Map<Long, Item> itemMap = items.stream()
                .collect(Collectors.toMap(Item::getId, item -> item));

        OOTD ootd = OOTDConverter.toOOTD(
                requestDTO,
                user,
                style,
                tpo,
                backgroundColor
        );
        ootdRepository.save(ootd);

        List<OOTDItem> ootdItems =
                OOTDConverter.toOOTDItems(
                        requestDTO.items(),
                        ootd,
                        itemMap
                );
        ootdItemRepository.saveAll(ootdItems);

        return ootd.getId();
    }
}