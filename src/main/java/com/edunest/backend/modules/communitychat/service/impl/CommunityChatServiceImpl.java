package com.edunest.backend.modules.communitychat.service.impl;

import java.time.LocalDateTime;
import java.util.regex.Pattern;
import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.modules.branch.entity.Branch;
import com.edunest.backend.modules.college.entity.College;
import com.edunest.backend.modules.communitychat.dto.request.*;
import com.edunest.backend.modules.communitychat.dto.response.CommunityMessageResponse;
import com.edunest.backend.modules.communitychat.entity.*;
import com.edunest.backend.modules.communitychat.repository.*;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;
import com.edunest.backend.modules.userprofile.entity.UserAcademicProfile;
import com.edunest.backend.modules.userprofile.repository.UserAcademicProfileRepository;
import com.edunest.backend.modules.collegebranch.repository.CollegeBranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class CommunityChatServiceImpl implements CommunityChatService {
 private static final int MAX_PAGE=50, MAX_MESSAGES_PER_MINUTE=20;
 private static final Pattern URL=Pattern.compile("(?i)\b(?:https?://|www\.)\S+");
 private final CommunityChannelRepository channelRepository;
 private final CommunityMessageRepository messageRepository;
 private final CommunityReportRepository reportRepository;
 private final CommunityMuteRepository muteRepository;
 private final UserRepository userRepository;
 private final UserAcademicProfileRepository profileRepository;
 private final CollegeBranchRepository collegeBranchRepository;

 @Override public Page<CommunityMessageResponse> history(Long uid,Long cid,int page,int size){authorize(uid,cid); return messageRepository.findByChannel_IdAndStatusOrderByCreatedAtDesc(cid,CommunityMessageStatus.ACTIVE,PageRequest.of(Math.max(0,page),Math.min(Math.max(1,size),MAX_PAGE))).map(this::response);}
 @Override public Page<CommunityMessageResponse> thread(Long uid,Long cid,Long mid,int page,int size){authorize(uid,cid); CommunityMessage root=getMessage(mid,cid); return messageRepository.findThread(cid,root.getId(),PageRequest.of(Math.max(0,page),Math.min(Math.max(1,size),MAX_PAGE))).map(this::response);}
 @Override @Transactional public CommunityMessageResponse send(Long uid,Long cid,SendMessageRequest r){
  User user=user(uid); CommunityChannel channel=authorize(uid,cid);
  if(muteRepository.existsByChannel_IdAndUser_IdAndExpiresAtAfter(cid,uid,LocalDateTime.now())) throw new AccessDeniedException("You are muted in this channel");
  if(messageRepository.countByAuthor_IdAndCreatedAtAfter(uid,LocalDateTime.now().minusMinutes(1))>=MAX_MESSAGES_PER_MINUTE) throw new BadRequestException("Message rate limit exceeded");
  String content=r.getContent().trim();
  if(URL.matcher(content).find()) throw new BadRequestException("External links are not allowed in community chat");
  CommunityMessage parent=null;
  if(r.getParentMessageId()!=null){parent=getMessage(r.getParentMessageId(),cid); if(parent.getStatus()!=CommunityMessageStatus.ACTIVE) throw new BadRequestException("Parent message is unavailable");}
  CommunityMessage m=CommunityMessage.builder().channel(channel).author(user).parentMessage(parent).content(content).status(CommunityMessageStatus.ACTIVE).pinned(false).build();
  return response(messageRepository.save(m));
 }
 @Override @Transactional public void delete(Long uid,Long cid,Long mid){CommunityChannel c=authorize(uid,cid); CommunityMessage m=getMessage(mid,cid); User u=user(uid); if(!m.getAuthor().getId().equals(uid)&&!isAdmin(u))throw new AccessDeniedException("You cannot delete this message"); m.setStatus(CommunityMessageStatus.DELETED);m.setContent("");m.setDeletedAt(LocalDateTime.now());m.setDeletedBy(u);}
 @Override @Transactional public void pin(Long uid,Long cid,Long mid,boolean pinned){CommunityChannel c=authorize(uid,cid);if(!isAdmin(user(uid)))throw new AccessDeniedException("Only moderators can pin messages");getMessage(mid,cid).setPinned(pinned);}
 @Override @Transactional public void report(Long uid,Long cid,Long mid,ReportMessageRequest r){authorize(uid,cid);CommunityMessage m=getMessage(mid,cid);if(reportRepository.existsByMessage_IdAndReporter_Id(mid,uid))throw new BadRequestException("You already reported this message");reportRepository.save(CommunityMessageReport.builder().message(m).reporter(user(uid)).reason(r.getReason().trim()).createdAt(LocalDateTime.now()).resolved(false).build());}
 @Override @Transactional public void mute(Long aid,Long cid,Long uid,long minutes){if(!isAdmin(user(aid)))throw new AccessDeniedException("Only moderators can mute users");if(minutes<1||minutes>10080)throw new BadRequestException("Mute duration must be between 1 minute and 7 days");CommunityChannel c=channel(cid);User target=user(uid);CommunityUserMute m=muteRepository.findByChannel_IdAndUser_Id(cid,uid).orElse(CommunityUserMute.builder().channel(c).user(target).mutedBy(user(aid)).build());m.setMutedBy(user(aid));m.setExpiresAt(LocalDateTime.now().plusMinutes(minutes));muteRepository.save(m);}
 @Override @Transactional public void unmute(Long aid,Long cid,Long uid){if(!isAdmin(user(aid)))throw new AccessDeniedException("Only moderators can unmute users");muteRepository.findByChannel_IdAndUser_Id(cid,uid).ifPresent(muteRepository::delete);}
 private CommunityChannel authorize(Long uid,Long cid){UserAcademicProfile p=profileRepository.findByUserIdAndActiveTrue(uid).orElseThrow(()->new AccessDeniedException("Active academic profile required"));CommunityChannel c=channel(cid);if(!c.isActive())throw new BadRequestException("Channel is inactive");if(!c.getCollege().getId().equals(p.getCollege().getId()))throw new AccessDeniedException("You do not have access to this college channel");if(c.getChannelType()==CommunityChannelType.BRANCH&& (c.getBranch()==null||!c.getBranch().getId().equals(p.getBranch().getId())))throw new AccessDeniedException("You do not have access to this branch channel");if(c.getChannelType()==CommunityChannelType.BRANCH&&!collegeBranchRepository.existsByCollege_IdAndBranch_IdAndActiveTrue(p.getCollege().getId(),p.getBranch().getId()))throw new AccessDeniedException("Branch is not active for this college");return c;}
 private CommunityChannel channel(Long id){return channelRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Community channel not found"));}
 private CommunityMessage getMessage(Long id,Long cid){return messageRepository.findByIdAndChannel_Id(id,cid).orElseThrow(()->new ResourceNotFoundException("Message not found"));}
 private User user(Long id){return userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User not found"));}
 private boolean isAdmin(User u){return u.getRole()!=null&&"ADMIN".equals(u.getRole().getName().name());}
 private CommunityMessageResponse response(CommunityMessage m){return CommunityMessageResponse.builder().id(m.getId()).channelId(m.getChannel().getId()).authorId(m.getAuthor().getId()).authorName(m.getAuthor().getFullName()).parentMessageId(m.getParentMessage()==null?null:m.getParentMessage().getId()).content(m.getContent()).status(m.getStatus().name()).pinned(m.isPinned()).createdAt(m.getCreatedAt()).updatedAt(m.getUpdatedAt()).build();}
}