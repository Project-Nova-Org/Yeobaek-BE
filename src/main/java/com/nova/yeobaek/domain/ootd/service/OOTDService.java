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
        Style style = findStyle(requestDTO.styleId());
        TPO tpo = findTpo(requestDTO.tpoId());
        ImageBackgroundColor backgroundColor = parseBackground(requestDTO.imageBackground());
        List<Long> itemIds = extractItemIds(requestDTO);
        validateDuplicatedItemIds(itemIds);
        Map<Long, Item> itemMap = loadItemsOrThrow(itemIds);
        OOTD ootd = createAndSaveOOTD(requestDTO, user, style, tpo, backgroundColor);
        saveOOTDItems(requestDTO, ootd, itemMap);
        return ootd.getId();
    }

    private Style findStyle(Long styleId) {
        return styleRepository.findById(styleId)
                .orElseThrow(() -> new OOTDException(OOTDErrorStatus.STYLE_NOT_FOUND));
    }

    private TPO findTpo(Long tpoId) {
        return tpoRepository.findById(tpoId)
                .orElseThrow(() -> new OOTDException(OOTDErrorStatus.TPO_NOT_FOUND));
    }

    private ImageBackgroundColor parseBackground(String value) {
        try {
            return ImageBackgroundColor.from(value);
        } catch (IllegalArgumentException e) {
            throw new OOTDException(OOTDErrorStatus.INVALID_IMAGE_BACKGROUND);
        }
    }

    private List<Long> extractItemIds(OOTDRequestDTO.Create requestDTO) {
        return requestDTO.items().stream()
                .map(OOTDRequestDTO.Item::fashionItemId)
                .toList();
    }

    private void validateDuplicatedItemIds(List<Long> itemIds) {
        if (itemIds.size() != itemIds.stream().distinct().count()) {
            throw new OOTDException(OOTDErrorStatus.DUPLICATED_ITEM_ID);
        }
    }

    private Map<Long, Item> loadItemsOrThrow(List<Long> itemIds) {
        List<Item> items = itemRepository.findAllById(itemIds);
        if (items.size() != itemIds.size()) {
            throw new OOTDException(OOTDErrorStatus.ITEM_NOT_FOUND);
        }
        return items.stream()
                .collect(Collectors.toMap(Item::getId, item -> item));
    }

    private OOTD createAndSaveOOTD(OOTDRequestDTO.Create requestDTO, User user, Style style, TPO tpo, ImageBackgroundColor bg) {
        OOTD ootd = OOTDConverter.toOOTD(
                requestDTO,
                user,
                style,
                tpo,
                bg
        );
        ootdRepository.save(ootd);
        return ootd;
    }

    private void saveOOTDItems(OOTDRequestDTO.Create requestDTO, OOTD ootd, Map<Long, Item> itemMap) {
        List<OOTDItem> ootdItems = OOTDConverter.toOOTDItems(
                requestDTO.items(),
                ootd,
                itemMap
        );
        ootdItemRepository.saveAll(ootdItems);
    }
}