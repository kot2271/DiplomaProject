package blog.dto.responseDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OnePostDto extends PostDto {
  private String text;

  private List<CommentToPostDto> comments;

  private List<String> tags;

  public OnePostDto(
      Integer id,
      LocalDateTime time,
      UserToPostDto user,
      String title,
      String announce,
      Integer likeCount,
      Integer dislikeCount,
      Integer commentCount,
      Integer viewCount,
      String text,
      List<CommentToPostDto> comments,
      List<String> tags) {
    super(id, time, user, title, announce, viewCount, likeCount, dislikeCount, commentCount);
    this.text = text;
    this.comments = comments;
    this.tags = tags;
  }
}
