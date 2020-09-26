package blog.model;

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
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_moderator", nullable = false)
    private byte isModerator;

    @Column(name = "reg_time", nullable = false)
    private Date regTime;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String code;

    @Column(name = "photo", length = 66500, columnDefinition = "TEXT")
    private String photo;

    @OneToMany(mappedBy = "users")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "users")
    private List<PostComment> postComments = new ArrayList<>();

    public User(byte isModerator, Date regTime, String name, String email, String password, String code, String photo) {
    }
}
