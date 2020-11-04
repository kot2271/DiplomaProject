package blog.service;

import blog.exceptions.UnauthorizedException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;

@Service
@AllArgsConstructor
public class AuthService {

  private final Map<String, Integer> authorizedUsers;

  public void saveSession(Integer userId) {
    String sessionId1 = RequestContextHolder.currentRequestAttributes().getSessionId();
    authorizedUsers.put(sessionId1, userId);
  }

  public Integer getUserIdOnSessionId() {
    String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
    if (authorizedUsers.containsKey(sessionId)) {
      return authorizedUsers.get(sessionId);
    }
    return null;
  }

  public void deleteSession() {
    String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
    authorizedUsers.remove(sessionId);
  }

  public void checkAuth(Integer userId) {
    if (userId == null) {
      throw new UnauthorizedException();
    }
  }
}
