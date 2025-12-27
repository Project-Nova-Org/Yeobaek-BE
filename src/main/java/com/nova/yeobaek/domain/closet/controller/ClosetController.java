package com.nova.yeobaek.domain.closet.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nova.yeobaek.domain.closet.controller.docs.ClosetControllerDocs;
import com.nova.yeobaek.domain.closet.service.ClosetService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/closets")
public class ClosetController implements ClosetControllerDocs {

	private final ClosetService closetService;
}
