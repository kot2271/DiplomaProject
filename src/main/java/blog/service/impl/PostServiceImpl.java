package blog.service.impl;

import blog.model.Post;
import blog.repository.PostRepository;
import blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {

  @Autowired
  private PostRepository postRepository;

  @Override
  public Post getPost(int id) {
    return postRepository.getById(id);
  }
}
