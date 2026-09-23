package com.edunest.backend.modules.realtime.presence;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
@Service
public class PresenceService {
 private final Set<String> onlineUsers=ConcurrentHashMap.newKeySet();
 public void online(String username){onlineUsers.add(username);}
 public void offline(String username){onlineUsers.remove(username);}
 public boolean isOnline(String username){return onlineUsers.contains(username);}
}