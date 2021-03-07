package blog.model;

import blog.model.enums.GlobalSetting;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "global_settings")
public class GlobalSettings {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Enumerated(EnumType.STRING)
  private GlobalSetting code;

  private String name;
  private Boolean value;
}
