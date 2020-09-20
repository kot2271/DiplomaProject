package blog.mappers;

import blog.dto.PostDTO;
import blog.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PostMapper {

@Mapping(source = "user", target = "UserDTO")
PostDTO toDTO (Post post);
}
