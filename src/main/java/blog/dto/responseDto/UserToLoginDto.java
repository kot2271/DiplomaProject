package blog.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserToLoginDto {
  private Integer id;
  private String name;
  private String photo;
  private String email;
  private boolean moderation;
  private Integer moderationCount;
  private boolean settings;

}
