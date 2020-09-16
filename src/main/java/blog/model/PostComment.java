package blog.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "post_comment")
public class PostComment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private User userId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post postId;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column
    private String text;
}
