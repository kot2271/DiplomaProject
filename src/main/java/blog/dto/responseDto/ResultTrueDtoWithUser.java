package blog.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResultTrueDtoWithUser {
    private boolean result = true;
    @JsonProperty("user")
    private UserToLoginDto user;

    public ResultTrueDtoWithUser(UserToLoginDto userToLoginDto) {
        this.user = userToLoginDto;
    }

}
