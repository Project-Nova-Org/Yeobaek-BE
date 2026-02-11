package com.nova.yeobaek.domain.closet.controller.docs;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.nova.yeobaek.domain.closet.dto.request.ClosetEditRequestDTO;
import com.nova.yeobaek.domain.closet.dto.request.ClosetItemQuery;
import com.nova.yeobaek.domain.closet.dto.request.ClosetRequestDTO;
import com.nova.yeobaek.domain.closet.dto.response.ClosetEditResponseDTO;
import com.nova.yeobaek.domain.closet.dto.response.ClosetResponseDTO;
import com.nova.yeobaek.domain.closet.dto.type.ClosetSortType;
import com.nova.yeobaek.global.auth.security.CustomUserDetails;
import com.nova.yeobaek.global.payload.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Closet", description = "옷장 API")
public interface ClosetControllerDocs {

    @Operation(
            summary = "옷장 생성 API",
            description = "로그인한 사용자의 옷장을 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "옷장 생성 성공"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "이름에 특수문자 포함",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "INVALID_NAME", value = """
                        {
                          "success": false,
                          "code": "CLOSET_4001",
                          "message": "특수문자는 사용할 수 없습니다.",
                          "timestamp": "2026-01-14T10:00:00"
                        }
                        """))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "아이템 미포함",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "EMPTY_ITEMS", value = """
                        {
                          "success": false,
                          "code": "CLOSET_4002",
                          "message": "하나 이상의 아이템이 포함되어야 합니다.",
                          "timestamp": "2026-01-14T10:00:00"
                        }
                        """))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 필요",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "UNAUTHORIZED", value = """
                        {
                          "success": false,
                          "code": "AUTH_401",
                          "message": "로그인이 필요합니다."
                        }
                        """))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "SERVER_ERROR", value = """
                        {
                          "success": false,
                          "code": "CLOSET_5001",
                          "message": "서버 오류 발생."
                        }
                        """))
                    )
            }
    )
    CommonResponse<ClosetResponseDTO.Create> createCloset(
            CustomUserDetails userDetails,
            @RequestBody ClosetRequestDTO.Create request
    );

    @Operation(
            summary = "옷장 리스트 조회 API",
            description = "로그인한 사용자의 옷장 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "401", description = "인증 필요"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    CommonResponse<ClosetResponseDTO.CursorListResponse> getClosets(
            CustomUserDetails userDetails,

            @Parameter(description = "이전 페이지 마지막 closetId(없으면 첫 페이지)")
            @RequestParam(required = false) Long cursorId,

            @Parameter(description = "이전 페이지 마지막 favorite(복합정렬 시 필요)")
            @RequestParam(required = false) Boolean cursorFavorite,

            @Parameter(description = "페이지 크기 (1~100)")
            @RequestParam(defaultValue = "20") Integer size,

            @Parameter(description = "정렬 방식")
            @RequestParam(defaultValue = "LATEST") ClosetSortType sort
    );

    @Operation(
            summary = "옷장 상세 조회 API",
            description = "로그인한 사용자의 특정 옷장을 상세 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "옷장 없음(또는 접근 불가)",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "CLOSET_NOT_FOUND", value = """
                        {
                          "success": false,
                          "code": "CLOSET_4041",
                          "message": "옷장을 찾을 수 없습니다.",
                          "timestamp": "2026-01-28T10:00:00"
                        }
                        """))
                    ),
                    @ApiResponse(responseCode = "401", description = "인증 필요"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    CommonResponse<ClosetResponseDTO.Detail> getClosetDetail(
            CustomUserDetails userDetails,
            @PathVariable Long closetId
    );

    @Operation(
            summary = "옷장 즐겨찾기 설정/해제 API",
            description = "로그인한 사용자의 옷장 즐겨찾기를 설정/해제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "수정 성공"),
                    @ApiResponse(responseCode = "401", description = "인증 필요"),
                    @ApiResponse(responseCode = "404", description = "옷장 없음(또는 접근 불가)"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    CommonResponse<ClosetResponseDTO.FavoriteUpdate> updateFavorite(
            CustomUserDetails userDetails,
            @PathVariable Long closetId,
            @RequestBody ClosetRequestDTO.FavoriteUpdate request
    );

    @Operation(
            summary = "옷장 아이템 목록 조회 API",
            description = "특정 옷장에 포함된 아이템을 커서 기반으로 조회합니다. 카테고리(종류/세부분류) 필터를 지원합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "옷장 없음(또는 접근 불가)",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "CLOSET_NOT_FOUND", value = """
                        {
                          "success": false,
                          "code": "CLOSET_4041",
                          "message": "옷장을 찾을 수 없습니다.",
                          "timestamp": "2026-01-28T10:00:00"
                        }
                        """))
                    ),
                    @ApiResponse(responseCode = "401", description = "인증 필요"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    CommonResponse<ClosetResponseDTO.ClosetItemCursorListResponse> getClosetItems(
            CustomUserDetails userDetails,
            @Parameter(description = "옷장 ID") @PathVariable Long closetId,
            @Parameter(description = "커서/페이지/카테고리 필터 파라미터 묶음 (size: 1~100, 미전달 시 20)")
            @ModelAttribute ClosetItemQuery query
    );

    @Operation(
            summary = "옷장 삭제 API",
            description = "로그인한 사용자의 옷장을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "삭제 성공"),
                    @ApiResponse(responseCode = "401", description = "인증 필요"),
                    @ApiResponse(responseCode = "404", description = "옷장 없음(또는 접근 불가)"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    CommonResponse<ClosetResponseDTO.DeleteResult> deleteCloset(
            CustomUserDetails userDetails,
            @PathVariable Long closetId
    );


    @Operation(
            summary = "옷장 수정 진입 정보 조회 API",
            description = "옷장 수정(이름/썸네일 설정 화면) 진입에 필요한 옷장 정보와 선택된 아이템 ID 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "401", description = "인증 필요"),
                    @ApiResponse(responseCode = "404", description = "옷장 없음(또는 접근 불가)"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    CommonResponse<ClosetEditResponseDTO.EditInfo> getClosetEditInfo(
            CustomUserDetails userDetails,
            @PathVariable Long closetId
    );

    @Operation(
            summary = "옷장 수정용 아이템 선택 목록 조회 API",
            description = "아이템 선택 화면에서 사용할 전체 아이템 목록을 커서 기반으로 조회합니다. 옷장에 포함된 아이템은 selectedStatus=true 입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "401", description = "인증 필요"),
                    @ApiResponse(responseCode = "404", description = "옷장 없음(또는 접근 불가)"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    CommonResponse<ClosetEditResponseDTO.EditableItemCursorList> getEditableItems(
            CustomUserDetails userDetails,
            @PathVariable Long closetId,
            @ModelAttribute ClosetItemQuery query
    );

    @Operation(
            summary = "옷장 수정 저장 API",
            description = "옷장 이름/썸네일을 수정하고, 요청 itemIds 기준으로 옷장 아이템을 동기화(추가/제거)합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "수정 성공"),
                    @ApiResponse(responseCode = "400", description = "요청 값 오류(중복 아이템/존재하지 않는 아이템 등)"),
                    @ApiResponse(responseCode = "401", description = "인증 필요"),
                    @ApiResponse(responseCode = "404", description = "옷장 없음(또는 접근 불가)"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    CommonResponse<ClosetEditResponseDTO.UpdateResult> updateCloset(
            CustomUserDetails userDetails,
            @PathVariable Long closetId,
            @RequestBody ClosetEditRequestDTO.Update request
    );
}
