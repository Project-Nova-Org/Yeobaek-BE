package com.nova.yeobaek.domain.ootd.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nova.yeobaek.domain.ootd.controller.docs.OOTDControllerDocs;
import com.nova.yeobaek.domain.ootd.service.OOTDService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ootds")
public class OOTDController implements OOTDControllerDocs {

	private final OOTDService ootdService;
}
