package blog.mappers;

import blog.dto.UserDTO;
import blog.model.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

   UserDTO toDTO (User user);

}
