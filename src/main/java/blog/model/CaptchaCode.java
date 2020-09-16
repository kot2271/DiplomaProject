package blog.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "captcha_code")
public class CaptchaCode implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String secretCode;
}
