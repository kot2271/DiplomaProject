package blog.mappers;

import blog.dto.responseDto.UserToPostDto;
import blog.dto.responseDto.UserToPostDto.UserDTOBuilder;
import blog.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-09-24T19:44:58+0300",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.6 (JetBrains s.r.o)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserToPostDto toDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTOBuilder userDTO = UserToPostDto.builder();

        userDTO.id( user.getId() );
        userDTO.name( user.getName() );

        return userDTO.build();
    }
}
