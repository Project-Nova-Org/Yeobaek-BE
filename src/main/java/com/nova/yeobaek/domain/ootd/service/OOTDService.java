package com.nova.yeobaek.domain.ootd.service;

import com.nova.yeobaek.domain.ootd.converter.OOTDConverter;
import com.nova.yeobaek.domain.ootd.domain.OOTD;
import com.nova.yeobaek.domain.ootd.domain.mapping.OOTDItem;
import com.nova.yeobaek.domain.ootd.domain.Style;
import com.nova.yeobaek.domain.ootd.domain.TPO;
import com.nova.yeobaek.domain.ootd.dto.request.OOTDRequestDTO;
import com.nova.yeobaek.domain.ootd.repository.OOTDRepository;
import com.nova.yeobaek.domain.ootd.repository.ootdItem.OOTDItemRepository;
import com.nova.yeobaek.domain.ootd.repository.style.StyleRepository;
import com.nova.yeobaek.domain.ootd.repository.tpo.TPORepository;
import com.nova.yeobaek.global.payload.status.CommonErrorStatus;
import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.service.UserService;
import com.nova.yeobaek.global.payload.exception.GeneralException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    /**
     * OOTD 등록
     */
    @Transactional
    public Long createOOTD(OOTDRequestDTO requestDTO) {

        User user = null; // TODO: 인증 연동 시 수정

        Style style = styleRepository.findById(requestDTO.getStyleId())
                .orElseThrow(() -> new GeneralException(CommonErrorStatus._BAD_REQUEST));

        TPO tpo = tpoRepository.findById(requestDTO.getTpoId())
                .orElseThrow(() -> new GeneralException(CommonErrorStatus._BAD_REQUEST));

        OOTD ootd = OOTDConverter.toOOTD(requestDTO, user, style, tpo);
        ootdRepository.save(ootd);

        List<OOTDItem> ootdItems =
                OOTDConverter.toOOTDItems(requestDTO.getItems(), ootd);
        ootdItemRepository.saveAll(ootdItems);

        return ootd.getId();
    }
}