package blog.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "post_comments")
public class PostComment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "parent_id")
  private Integer parentId;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @Column(nullable = false)
  private Date time;

  @Column(name = "text", nullable = false, length = 65600)
  private String text;

  public PostComment() {}

  public PostComment(Integer parentId, Post post, User user, Date time, String text) {
    this.parentId = parentId;
    this.user = user;
    this.post = post;
    this.time = time;
    this.text = text;
  }
}
