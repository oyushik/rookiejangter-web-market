package com.miniproject.rookiejangter.controller;

import com.miniproject.rookiejangter.dto.MessageDTO;
import com.miniproject.rookiejangter.dto.MessageDTO.ApiResponseWrapper;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.service.MessageService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/chats/{chatRoomId}/messages") // 채팅의 하위 리소스로 메시지 정의
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class MessageController {

    private final MessageService messageService;

    /**
     * 특정 채팅방에 메시지를 전송합니다.
     *
     * @param chatRoomId 메시지를 전송할 채팅방 ID
     * @param request 메시지 내용 요청 DTO
     * @param principal 현재 로그인한 사용자 정보 (JWT 토큰에서 추출)
     * @return 전송된 메시지 정보
     */
    @PostMapping
    public ResponseEntity<ApiResponseWrapper<MessageDTO.Response>> sendMessage(
            @PathVariable Long chatRoomId,
            @Valid @RequestBody MessageDTO.Request request,
            Principal principal) {
        try {
            Long senderId = Long.valueOf(principal.getName()); // JWT 토큰의 subject (사용자 ID) 추출
            MessageDTO.Response response = messageService.sendMessage(chatRoomId, request, senderId);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseWrapper.<MessageDTO.Response>builder()
                    .success(true)
                    .data(response)
                    .content("메시지가 성공적으로 전송되었습니다.")
                    .build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(ApiResponseWrapper.<MessageDTO.Response>builder()
                    .success(false)
                    .error(e.getMessage())
                    .content(e.getErrorCode().getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponseWrapper.<MessageDTO.Response>builder()
                    .success(false)
                    .error(e.getMessage())
                    .content("메시지 전송 중 오류가 발생했습니다.")
                    .build());
        }
    }

    /**
     * 특정 채팅방의 메시지 목록을 조회합니다.
     *
     * @param chatRoomId 메시지 목록을 조회할 채팅방 ID
     * @param pageable 페이징 및 정렬 파라미터 (page, size, sort)
     * @param principal 현재 로그인한 사용자 정보 (JWT 토큰에서 추출)
     * @return 채팅방의 메시지 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<MessageDTO.MessageListResponse>> getMessagesByChatId(
            @PathVariable Long chatRoomId,
            @PageableDefault(size = 20, sort = "sentAt", direction = Sort.Direction.ASC) Pageable pageable,
            Principal principal) {
        try {
            Long currentUserId = Long.valueOf(principal.getName()); // 메시지 읽음 처리에 사용될 현재 사용자 ID
            MessageDTO.MessageListResponse response = messageService.getMessagesByChatId(chatRoomId, pageable);

            // 메시지 조회 후, 해당 채팅방의 모든 읽지 않은 메시지를 현재 사용자가 읽음 처리
            // (즉, 현재 사용자가 '수신자'인 메시지만 읽음 처리)
            messageService.markAllMessagesAsRead(chatRoomId, currentUserId);

            return ResponseEntity.ok(ApiResponseWrapper.<MessageDTO.MessageListResponse>builder()
                    .success(true)
                    .data(response)
                    .content("메시지 목록을 성공적으로 조회했습니다.")
                    .build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(ApiResponseWrapper.<MessageDTO.MessageListResponse>builder()
                    .success(false)
                    .error(e.getMessage())
                    .content(e.getErrorCode().getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponseWrapper.<MessageDTO.MessageListResponse>builder()
                    .success(false)
                    .error(e.getMessage())
                    .content("메시지 목록 조회 중 오류가 발생했습니다.")
                    .build());
        }
    }

    /**
     * 특정 메시지를 읽음 상태로 변경합니다. (선택적)
     * 이 엔드포인트는 개별 메시지를 명시적으로 읽음 처리할 때 사용될 수 있습니다.
     *
     * @param chatRoomId 채팅방 ID (경로 일관성을 위해 추가)
     * @param messageId 읽음 처리할 메시지 ID
     * @return 읽음 처리 성공 여부
     */
    @PatchMapping("/{messageId}/read")
    public ResponseEntity<ApiResponseWrapper<Void>> markMessageAsRead(
            @PathVariable Long chatRoomId, // chatRoomId는 URL 경로 일관성을 위해 존재하지만, 실제 로직에서는 messageId만 사용
            @PathVariable Long messageId) {
        try {
            messageService.markMessageAsRead(messageId);
            return ResponseEntity.ok(ApiResponseWrapper.<Void>builder()
                    .success(true)
                    .content("메시지가 성공적으로 읽음 처리되었습니다.")
                    .build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error(e.getMessage())
                    .content(e.getErrorCode().getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error(e.getMessage())
                    .content("메시지 읽음 처리 중 오류가 발생했습니다.")
                    .build());
        }
    }
}