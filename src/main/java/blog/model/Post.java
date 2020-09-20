package blog.model;

import blog.model.enums.ModerationStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "posts")
public class Post  {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "is_active", nullable = false)
  private byte isActive;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ModerationStatus moderationStatus = ModerationStatus.NEW;

  @Column(name = "moderator_id")
  private Integer moderatorId;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false)
  private Date time;

  @Column(nullable = false)
  private String title;

  @Column(name = "text", length = 65600, nullable = false)
  private String text;

  @Column(name = "view_count")
  private int viewCount;

  @OneToMany(mappedBy = "posts")
  private List<PostComment> postComments = new ArrayList<>();

  @OneToOne(mappedBy = "posts")
  private Tag2Post tag2Post;

  public Post() {}

  public Post(
      byte isActive,
      ModerationStatus moderationStatus,
      Integer moderatorId,
      User user,
      Date time,
      String title,
      String text,
      int viewCount) {
    this.isActive = isActive;
    this.moderationStatus = moderationStatus;
    this.moderatorId = moderatorId;
    this.user = user;
    this.time = time;
    this.title = title;
    this.text = text;
    this.viewCount = viewCount;
  }

  @Override
  public String toString() {
    return "Post{" + title + "}";
  }
}
