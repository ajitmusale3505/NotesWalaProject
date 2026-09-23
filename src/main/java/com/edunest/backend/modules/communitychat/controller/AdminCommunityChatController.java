package com.edunest.backend.modules.communitychat.controller;
import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.communitychat.service.CommunityChatService;
import com.edunest.backend.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/admin/community/channels") @RequiredArgsConstructor
public class AdminCommunityChatController {
 private final CommunityChatService service;
 @PostMapping("/{channelId}/users/{userId}/mute") public ResponseEntity<ApiResponse<Void>> mute(@PathVariable Long channelId,@PathVariable Long userId,@RequestParam(defaultValue="60") long minutes){service.mute(SecurityUtils.getCurrentUserId(),channelId,userId,minutes);return empty("User muted successfully");}
 @DeleteMapping("/{channelId}/users/{userId}/mute") public ResponseEntity<ApiResponse<Void>> unmute(@PathVariable Long channelId,@PathVariable Long userId){service.unmute(SecurityUtils.getCurrentUserId(),channelId,userId);return empty("User unmuted successfully");}
 private ResponseEntity<ApiResponse<Void>> empty(String m){return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message(m).build());}
}