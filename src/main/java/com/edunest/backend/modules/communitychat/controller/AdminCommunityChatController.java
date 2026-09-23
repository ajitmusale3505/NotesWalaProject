package com.edunest.backend.modules.communitychat.controller;
import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.communitychat.service.CommunityChatService;
import com.edunest.backend.modules.communitychat.entity.CommunityMessageReport;
import org.springframework.data.domain.Page;
import com.edunest.backend.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/admin/community/channels") @RequiredArgsConstructor
public class AdminCommunityChatController {
 private final CommunityChatService service;
 @GetMapping("/reports") public ResponseEntity<ApiResponse<Page<CommunityMessageReport>>> reports(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return ResponseEntity.ok(ApiResponse.<Page<CommunityMessageReport>>builder().success(true).message("Pending reports fetched").data(service.pendingReports(SecurityUtils.getCurrentUserId(),page,size)).build());}
 @PostMapping("/reports/{reportId}/resolve") public ResponseEntity<ApiResponse<Void>> resolve(@PathVariable Long reportId){service.resolveReport(SecurityUtils.getCurrentUserId(),reportId);return empty("Report resolved successfully");}
 @PostMapping("/{channelId}/users/{userId}/mute") public ResponseEntity<ApiResponse<Void>> mute(@PathVariable Long channelId,@PathVariable Long userId,@RequestParam(defaultValue="60") long minutes){service.mute(SecurityUtils.getCurrentUserId(),channelId,userId,minutes);return empty("User muted successfully");}
 @DeleteMapping("/{channelId}/users/{userId}/mute") public ResponseEntity<ApiResponse<Void>> unmute(@PathVariable Long channelId,@PathVariable Long userId){service.unmute(SecurityUtils.getCurrentUserId(),channelId,userId);return empty("User unmuted successfully");}
 private ResponseEntity<ApiResponse<Void>> empty(String m){return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message(m).build());}
}