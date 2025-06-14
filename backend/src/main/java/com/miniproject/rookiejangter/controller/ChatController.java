package com.miniproject.rookiejangter.controller;

import com.miniproject.rookiejangter.dto.ChatDTO;
import com.miniproject.rookiejangter.dto.ChatDTO.ApiResponseWrapper;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    private final ChatService chatService;

    /**
     * 새로운 채팅방을 생성합니다.
     * 로그인한 사용자가 구매자, 요청 DTO의 sellerId가 판매자가 됩니다.
     *
     * @param request 채팅방 생성 요청 DTO (판매자 ID, 상품 ID 포함)
     * @param principal 현재 로그인한 사용자 정보 (JWT 토큰에서 추출)
     * @return 생성된 채팅방 정보
     */
    @PostMapping
    public ResponseEntity<ApiResponseWrapper<ChatDTO.Response>> createChat(
            @RequestBody ChatDTO.Request request,
            Principal principal) {
        try {
            Long buyerId = Long.valueOf(principal.getName()); // JWT 토큰의 subject (사용자 ID) 추출
            ChatDTO.Response response = chatService.createChat(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseWrapper.<ChatDTO.Response>builder()
                    .success(true)
                    .data(response)
                    .message("채팅방이 성공적으로 생성되었습니다.")
                    .build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(ApiResponseWrapper.<ChatDTO.Response>builder()
                    .success(false)
                    .error(e.getMessage())
                    .message(e.getErrorCode().getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponseWrapper.<ChatDTO.Response>builder()
                    .success(false)
                    .error(e.getMessage())
                    .message("채팅방 생성 중 오류가 발생했습니다.")
                    .build());
        }
    }

    /**
     * 특정 ID의 채팅방 정보를 조회합니다.
     *
     * @param chatId 조회할 채팅방 ID
     * @return 조회된 채팅방 정보
     */
    @GetMapping("/{chatId}")
    public ResponseEntity<ApiResponseWrapper<ChatDTO.Response>> getChatById(@PathVariable Long chatId) {
        try {
            ChatDTO.Response response = chatService.getChatById(chatId);
            return ResponseEntity.ok(ApiResponseWrapper.<ChatDTO.Response>builder()
                    .success(true)
                    .data(response)
                    .message("채팅방 정보를 성공적으로 조회했습니다.")
                    .build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(ApiResponseWrapper.<ChatDTO.Response>builder()
                    .success(false)
                    .error(e.getMessage())
                    .message(e.getErrorCode().getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponseWrapper.<ChatDTO.Response>builder()
                    .success(false)
                    .error(e.getMessage())
                    .message("채팅방 정보 조회 중 오류가 발생했습니다.")
                    .build());
        }
    }

    /**
     * 현재 로그인한 사용자가 참여하고 있는 모든 채팅방 목록을 조회합니다.
     * 페이징 및 정렬을 지원합니다.
     *
     * @param principal 현재 로그인한 사용자 정보 (JWT 토큰에서 추출)
     * @param pageable 페이징 및 정렬 파라미터 (page, size, sort)
     * @return 사용자가 참여하고 있는 채팅방 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<ChatDTO.ChatListResponse>> getChatsByUserId(
            Principal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Long userId = Long.valueOf(principal.getName()); // JWT 토큰의 subject (사용자 ID) 추출
            ChatDTO.ChatListResponse response = chatService.getChatsByUserId(userId, pageable);
            return ResponseEntity.ok(ApiResponseWrapper.<ChatDTO.ChatListResponse>builder()
                    .success(true)
                    .data(response)
                    .message("채팅방 목록을 성공적으로 조회했습니다.")
                    .build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(ApiResponseWrapper.<ChatDTO.ChatListResponse>builder()
                    .success(false)
                    .error(e.getMessage())
                    .message(e.getErrorCode().getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponseWrapper.<ChatDTO.ChatListResponse>builder()
                    .success(false)
                    .error(e.getMessage())
                    .message("채팅방 목록 조회 중 오류가 발생했습니다.")
                    .build());
        }
    }

    /**
     * 특정 채팅방을 삭제합니다.
     *
     * @param chatId 삭제할 채팅방 ID
     * @return 삭제 성공 여부
     */
    @DeleteMapping("/{chatId}")
    public ResponseEntity<ApiResponseWrapper<Void>> deleteChat(@PathVariable Long chatId) {
        try {
            chatService.deleteChat(chatId);
            return ResponseEntity.ok(ApiResponseWrapper.<Void>builder()
                    .success(true)
                    .message("채팅방이 성공적으로 삭제되었습니다.")
                    .build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error(e.getMessage())
                    .message(e.getErrorCode().getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error(e.getMessage())
                    .message("채팅방 삭제 중 오류가 발생했습니다.")
                    .build());
        }
    }
}