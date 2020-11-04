package blog.model;

import blog.model.enums.GlobalSettings;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "global_settings")
public class GlobalSetting {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Enumerated(EnumType.STRING)
  private GlobalSettings code;
  private String name;
  private Boolean value;

}
