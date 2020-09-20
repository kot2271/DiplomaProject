package blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {

    private int id;
    private Date time;
    private String text;
    private int viewCount;
    private String title;

    @JsonProperty("user")
    private UserDTO userDTO;

}
