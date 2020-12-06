package blog.service;

import blog.dto.requestDto.AddCommentToPostDto;
import blog.model.Post;
import blog.model.PostComment;
import blog.model.User;
import blog.repository.PostCommentRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentService {
  private PostCommentRepository postCommentRepository;

  public Integer addNewCommentToPost(
      AddCommentToPostDto addCommentToPostDto, Post post, User user) {
    PostComment postComment = new PostComment();
    postComment.setPost(post);
    postComment.setParentId(addCommentToPostDto.getParentId());
    postComment.setText(addCommentToPostDto.getText());
    postComment.setTime(LocalDateTime.now());
    postComment.setUser(user);
    postCommentRepository.save(postComment);
    return postComment.getId();
  }

  @Transactional
  public PostComment getPostCommentByParentIdAndPostId(Integer parentId, Integer postId) {
    return postCommentRepository.findByIdAndPostId(parentId, postId);
  }
}
