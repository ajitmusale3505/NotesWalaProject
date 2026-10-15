package com.edunest.backend.modules.communitychat.controller;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.communitychat.dto.request.ReportMessageRequest;
import com.edunest.backend.modules.communitychat.dto.request.SendMessageRequest;
import com.edunest.backend.modules.communitychat.dto.response.CommunityAttachmentResponse;
import com.edunest.backend.modules.communitychat.dto.response.CommunityMemberResponse;
import com.edunest.backend.modules.communitychat.dto.response.CommunityMessageResponse;
import com.edunest.backend.modules.communitychat.service.CommunityChatService;
import com.edunest.backend.security.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/community/channels")
@RequiredArgsConstructor
public class CommunityChatController {

    private final CommunityChatService service;

    @GetMapping("/{channelId}/messages")
    public ResponseEntity<ApiResponse<Page<CommunityMessageResponse>>> history(
            @PathVariable Long channelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long beforeId) {
        var messages = beforeId == null
                ? service.history(SecurityUtils.getCurrentUserId(), channelId, page, size)
                : service.historyBefore(SecurityUtils.getCurrentUserId(), channelId, beforeId, size);
        return ok("Messages fetched successfully", messages);
    }

    @GetMapping("/{channelId}/members")
    public ResponseEntity<ApiResponse<Page<CommunityMemberResponse>>> members(
            @PathVariable Long channelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        return ok("Group members fetched successfully",
                service.members(SecurityUtils.getCurrentUserId(), channelId, page, size));
    }

    @GetMapping("/{channelId}/pinned")
    public ResponseEntity<ApiResponse<CommunityMessageResponse>> pinned(@PathVariable Long channelId) {
        return ok("Pinned message fetched successfully",
                service.pinned(SecurityUtils.getCurrentUserId(), channelId));
    }

    @GetMapping("/{channelId}/attachments")
    public ResponseEntity<ApiResponse<List<CommunityAttachmentResponse>>> attachments(
            @PathVariable Long channelId,
            @RequestParam(defaultValue = "5") int limit) {
        return ok("Shared files fetched successfully",
                service.attachments(SecurityUtils.getCurrentUserId(), channelId, limit));
    }

    @GetMapping("/{channelId}/messages/{messageId}/thread")
    public ResponseEntity<ApiResponse<Page<CommunityMessageResponse>>> thread(
            @PathVariable Long channelId,
            @PathVariable Long messageId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ok("Thread fetched successfully",
                service.thread(SecurityUtils.getCurrentUserId(), channelId, messageId, page, size));
    }

    @PostMapping("/{channelId}/messages")
    public ResponseEntity<ApiResponse<CommunityMessageResponse>> send(
            @PathVariable Long channelId,
            @Valid @RequestBody SendMessageRequest request) {
        return ok("Message sent successfully",
                service.send(SecurityUtils.getCurrentUserId(), channelId, request));
    }

    @PostMapping("/{channelId}/messages/{messageId}/attachments")
    public ResponseEntity<ApiResponse<CommunityAttachmentResponse>> attach(
            @PathVariable Long channelId,
            @PathVariable Long messageId,
            @RequestParam("file") MultipartFile file) {
        return ok("Attachment added successfully",
                service.attach(SecurityUtils.getCurrentUserId(), channelId, messageId, file));
    }

    @DeleteMapping("/{channelId}/messages/{messageId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long channelId, @PathVariable Long messageId) {
        service.delete(SecurityUtils.getCurrentUserId(), channelId, messageId);
        return empty("Message deleted successfully");
    }

    @PostMapping("/{channelId}/messages/{messageId}/pin")
    public ResponseEntity<ApiResponse<Void>> pin(
            @PathVariable Long channelId,
            @PathVariable Long messageId,
            @RequestParam(defaultValue = "true") boolean pinned) {
        service.pin(SecurityUtils.getCurrentUserId(), channelId, messageId, pinned);
        return empty("Message pin state updated");
    }

    @PostMapping("/{channelId}/messages/{messageId}/report")
    public ResponseEntity<ApiResponse<Void>> report(
            @PathVariable Long channelId,
            @PathVariable Long messageId,
            @Valid @RequestBody ReportMessageRequest request) {
        service.report(SecurityUtils.getCurrentUserId(), channelId, messageId, request);
        return empty("Message reported successfully");
    }

    private <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponse.<T>builder().success(true).message(message).data(data).build());
    }

    private <T> ResponseEntity<ApiResponse<T>> empty(String message) {
        return ResponseEntity.ok(ApiResponse.<T>builder().success(true).message(message).build());
    }
}
