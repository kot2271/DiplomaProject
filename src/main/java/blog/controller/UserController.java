package blog.controller;

import blog.dto.UserDTO;
import blog.mappers.UserMapper;
import blog.model.User;
import blog.repository.UserRepository;
import blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  private final UserMapper userMapper;

  private final UserRepository userRepository;


  @GetMapping("/")
  public List<UserDTO> getAllUsersDTO() {
    List<User> users = userRepository.findAll();

    List<UserDTO> usersDTO = new ArrayList<>();
    for (User user : users) {
      usersDTO.add(getUserDTOById(user.getId()));
    }
    return usersDTO;
  }

  @GetMapping("/{id}")
  public UserDTO getUserDTOById(@PathVariable int id) {
    User user = userService.getUser(id);
    return userMapper.toDTO(user);
  }
}
