package blog.model;

import blog.model.enums.GlobalSettingName;
import blog.model.enums.GlobalSettingValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "global_setting")
public class GlobalSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GlobalSettingName name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GlobalSettingValue value;


}
