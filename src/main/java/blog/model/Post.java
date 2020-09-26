package blog.model;

import blog.model.enums.ModerationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "post")
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

  @OneToMany(mappedBy = "post")
  private List<PostComment> postComments = new ArrayList<>();

  @OneToOne(mappedBy = "post")
  private Tag2Post tag2Post;

  @Override
  public String toString() {
    return "Post{" + title + "}";
  }
}
