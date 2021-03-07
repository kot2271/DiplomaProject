package blog.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddPostDto {

    private String time;
    private Byte active;
    private String title;
    private String text;
    private List<String> tags;
}