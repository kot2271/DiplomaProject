package blog.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

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

  public CaptchaCode() {}

  public CaptchaCode(Date time, String code, String secretCode) {
    this.time = time;
    this.code = code;
    this.secretCode = secretCode;
  }
}
