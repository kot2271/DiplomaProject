package blog.controller;

import blog.dto.PostDTO;
import blog.mappers.PostMapper;
import blog.model.Post;
import blog.repository.PostRepository;
import blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/post")
public class ApiPostController {

  private final PostRepository postRepository;

  private final PostService postService;

  private final PostMapper postMapper;

  @GetMapping("/{id}")
  public PostDTO getPostDTOById(@PathVariable int id) {
    Post post = postService.getPost(id);
    return postMapper.toDTO(post);
  }

  @GetMapping("/")
  public List<PostDTO> getAllPostsDTO() {
    postRepository.findAll();
    return new ArrayList<>();
  }
}
