package blog.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddPostDto {

    @FutureOrPresent
    private String time;
    @AssertTrue
    private Byte active;
    @Size(min = 15, max = 214748367, message = "text" + "Текст должен быть больше 15 символов")
    private String text;
    @NotNull(message = "title" + "Пустой заголовок :(")
    private String title;
    @NotEmpty
    private List<String> tags;
}
