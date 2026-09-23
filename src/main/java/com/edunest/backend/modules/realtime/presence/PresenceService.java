package com.edunest.backend.modules.realtime.presence;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
@Service
public class PresenceService {
 private final Set<Long> onlineUsers=ConcurrentHashMap.newKeySet();
 public void online(Long id){onlineUsers.add(id);}
 public void offline(Long id){onlineUsers.remove(id);}
 public boolean isOnline(Long id){return onlineUsers.contains(id);}
}