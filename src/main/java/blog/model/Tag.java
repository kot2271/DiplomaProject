package blog.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "tags")
    List<Tag2Post> tag2Posts = new ArrayList<>();

    public Tag(){}

    public Tag(String name) {
        this.name = name;
    }
}
