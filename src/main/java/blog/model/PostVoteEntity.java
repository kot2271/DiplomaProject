package blog.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "post_votes")
public class PostVoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false)
    private Date time;

    @Column(nullable = false)
    private byte value;

    public PostVoteEntity(){}


    public PostVoteEntity(User user, Post post, Date time, byte value) {
        this.user = user;
        this.post = post;
        this.time = time;
        this.value = value;
    }

}
