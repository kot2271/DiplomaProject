package blog.model;

import blog.model.enums.ModerationStatus;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "post")
public class Post implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ModerationStatus moderationStatus;

    @ManyToOne
    @JoinColumn(name = "moderator_id")
    private User moderatorId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "Text", nullable = false)
    private String text;

    @Column(nullable = false)
    private int viewCount;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "tag2post", joinColumns = {@JoinColumn(name = "post_id")}, inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    private Set<Tag> tags = new HashSet<>();


}
