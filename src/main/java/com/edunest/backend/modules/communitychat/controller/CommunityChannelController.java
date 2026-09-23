package com.edunest.backend.modules.communitychat.controller;
import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.branch.entity.Branch;
import com.edunest.backend.modules.branch.repository.BranchRepository;
import com.edunest.backend.modules.college.entity.College;
import com.edunest.backend.modules.college.repository.CollegeRepository;
import com.edunest.backend.modules.communitychat.entity.*;
import com.edunest.backend.modules.communitychat.repository.CommunityChannelRepository;
import com.edunest.backend.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/community/channels") @RequiredArgsConstructor
public class CommunityChannelController {
 private final CommunityChannelRepository repository;
 private final CollegeRepository collegeRepository;
 private final BranchRepository branchRepository;
 @PostMapping("/college/{collegeId}") public ResponseEntity<ApiResponse<Long>> createCollege(@PathVariable Long collegeId){
  if(!SecurityUtils.isAdmin())throw new AccessDeniedException("Only administrators can create community channels");
  College college=collegeRepository.findById(collegeId).orElseThrow();
  CommunityChannel c=repository.findByChannelTypeAndCollege_IdAndBranch_Id(CommunityChannelType.COLLEGE,collegeId,null).orElseGet(()->repository.save(CommunityChannel.builder().channelType(CommunityChannelType.COLLEGE).college(college).active(true).build()));
  return ResponseEntity.ok(ApiResponse.<Long>builder().success(true).message("College channel ready").data(c.getId()).build());
 }
 @PostMapping("/college/{collegeId}/branch/{branchId}") public ResponseEntity<ApiResponse<Long>> createBranch(@PathVariable Long collegeId,@PathVariable Long branchId){
  if(!SecurityUtils.isAdmin())throw new AccessDeniedException("Only administrators can create community channels");
  College college=collegeRepository.findById(collegeId).orElseThrow(); Branch branch=branchRepository.findById(branchId).orElseThrow();
  CommunityChannel c=repository.findByChannelTypeAndCollege_IdAndBranch_Id(CommunityChannelType.BRANCH,collegeId,branchId).orElseGet(()->repository.save(CommunityChannel.builder().channelType(CommunityChannelType.BRANCH).college(college).branch(branch).active(true).build()));
  return ResponseEntity.ok(ApiResponse.<Long>builder().success(true).message("Branch channel ready").data(c.getId()).build());
 }
}