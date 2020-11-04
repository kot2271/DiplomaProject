package blog.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddPostDto {

    private String time;
    private Byte active;
    private String text;
    private String title;
    private List<String> tags;
}
