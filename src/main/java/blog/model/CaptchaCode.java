package blog.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "captcha_codes")
public class CaptchaCode {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false)
  private Date time;

  @Column(nullable = false)
  private String code;

  @Column(name = "secret_code",nullable = false)
  @JsonProperty("secret_code")
  private String secretCode;

}
