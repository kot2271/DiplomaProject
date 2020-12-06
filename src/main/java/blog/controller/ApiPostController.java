package blog.controller;

import blog.dto.requestDto.AddPostDto;
import blog.dto.requestDto.LikeOrDislikeDto;
import blog.dto.responseDto.*;
import blog.exceptions.BadRequestException;
import blog.model.Post;
import blog.model.Tag;
import blog.model.User;
import blog.service.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping(value = "/api/post")
@AllArgsConstructor
@NoArgsConstructor
public class ApiPostController {

  private PostService postService;
  private AuthService authService;
  private UserService userService;
  private TagService tagService;
  private Tag2PostService tag2PostService;
  private PostVotesService postVotesService;
  private GlobalSettingService globalSettingService;

  @GetMapping
  public ResponseEntity<?> getAllPosts(
      @RequestParam Integer offset, @RequestParam Integer limit, @RequestParam String mode) {
    List<PostDto> postDtoList = postService.getAllPosts(offset, limit, mode);
    Integer countAllPosts = postService.getCountAllPosts();
    return ResponseEntity.ok(new PostListDto(countAllPosts, postDtoList, offset, limit, mode));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getPostById(@PathVariable Integer id) {
    Integer userId = authService.getUserIdOnSessionId();
    User user = userService.getUserById(userId);
    return ResponseEntity.ok(postService.getPostById(id, user));
  }

  @GetMapping("/byTag")
  public ResponseEntity<?> getPostsByTag(
      @RequestParam String tag, @RequestParam Integer offset, @RequestParam Integer limit) {
    List<PostDto> postDtoList = postService.getPostByTag(tag, offset, limit);
    Integer countPostsByTag = postService.getCountPostByTag(tag);
    return ResponseEntity.ok(new PostListDto(countPostsByTag, postDtoList, offset, limit, tag));
  }

  @GetMapping("/search")
  public ResponseEntity<?> getPostsBySearchQuery(
      @RequestParam Integer offset,
      @RequestParam Integer limit,
      @RequestParam(required = false) String query) {
    List<PostDto> postDtoList = postService.getPostsBySearchQuery(query, limit, offset);
    Integer countPostsBySearchQuery = postService.getCountPostsBySearchQuery(query);
    return ResponseEntity.ok(
        new PostListDto(countPostsBySearchQuery, postDtoList, offset, limit, query));
  }

  @GetMapping("/byDate")
  public ResponseEntity<?> getPostsByDate(
      @RequestParam String date, @RequestParam Integer offset, @RequestParam Integer limit) {
    List<PostDto> postDtoList = postService.getPostsByDate(date, offset, limit);
    Integer countPostsByDate = postService.getCountPostsByDate(date);
    return ResponseEntity.ok(new PostListDto(countPostsByDate, postDtoList, offset, limit, date));
  }

  @GetMapping("/my")
  public ResponseEntity<?> getMyPosts(
      @RequestParam Integer offset, @RequestParam Integer limit, @RequestParam String status) {
    Integer userId = authService.getUserIdOnSessionId();
    authService.checkAuth(userId);
    List<PostDto> myPostDtoList = postService.getMyPosts(offset, limit, userId, status);
    Integer countPosts = postService.getCountMyPosts(userId, status);
    return ResponseEntity.ok(new PostListDto(countPosts, myPostDtoList, offset, limit, status));
  }

  @PostMapping
  public ResponseEntity<?> addNewPost(@RequestBody AddPostDto addPostDto) {
    Integer userId = authService.getUserIdOnSessionId();
    authService.checkAuth(userId);
    ResultFalseWithErrorsDto errorsDto = checkPostsTitleAndText(addPostDto);
    if (errorsDto.getErrors().size() > 0) {
      return ResponseEntity.badRequest().body(errorsDto);
    }
    boolean preModeration = globalSettingService.getGlobalSetting().isPostPremoderation();
    User userFromDB = userService.getUserById(userId);
    canAddOrEditPost(userFromDB);
    Integer postId = postService.addNewPost(addPostDto, userFromDB, preModeration);
    List<Tag> tagList = tagService.tagsToPost(addPostDto.getTags());
    tag2PostService.addNewTags2Posts(postId, tagList);
    return ResponseEntity.ok(new ResultTrueDto());
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> editPostById(
      @PathVariable Integer id, @RequestBody AddPostDto addPostDto) {
    Integer userId = authService.getUserIdOnSessionId();
    authService.checkAuth(userId);
    ResultFalseWithErrorsDto errorsDto = checkPostsTitleAndText(addPostDto);
    if (errorsDto.getErrors().size() > 0) {
      return ResponseEntity.badRequest().body(errorsDto);
    }
    boolean preModeration = globalSettingService.getGlobalSetting().isPostPremoderation();
    User userFromDB = userService.getUserById(userId);
    Post post = postService.getPostFromPostId(id);
    postService.editPostById(addPostDto, post, userFromDB, preModeration);
    List<Tag> tagList = tagService.tagsToPost(addPostDto.getTags());
    tag2PostService.addNewTags2Posts(id, tagList);
    return ResponseEntity.ok(new ResultTrueDto());
  }

  @GetMapping("/moderation")
  public ResponseEntity<?> postsToModeration(
      @RequestParam Integer offset, @RequestParam Integer limit, @RequestParam String status) {
    Integer userId = authService.getUserIdOnSessionId();
    authService.checkAuth(userId);
    User userFromDB = userService.getUserById(userId);
//    if (userFromDB.getIsModerator() == 0) {
//      throw new BadRequestException("Зайдите в аккаунт модератора");
//    }
    if (userFromDB.getName().contains("USER")){
      throw new BadRequestException("Зайдите в аккаунт модератора");
    }

    PostListDto postListDto = postService.getPostsToModeration(offset, limit, status, userId);
    return ResponseEntity.ok(postListDto);
  }

  @PostMapping("/like")
  public ResponseEntity<?> takeLikeToPost(@RequestBody LikeOrDislikeDto likeOrDislikeDto) {
    return takeLikeOrDislike(likeOrDislikeDto.getPostId(), 1);
  }

  @PostMapping("/dislike")
  public ResponseEntity<?> takeDislikeToPost(@RequestBody LikeOrDislikeDto likeOrDislikeDto) {
    return takeLikeOrDislike(likeOrDislikeDto.getPostId(), -1);
  }

  private ResponseEntity<?> takeLikeOrDislike(Integer postId, Integer likeOrDislike) {
    Integer userId = authService.getUserIdOnSessionId();
    authService.checkAuth(userId);
    Post post = postService.getPostFromPostId(postId);
    if (post != null && postVotesService.takeLikeOrDislikeToPost(post, userId, likeOrDislike)) {
      return ResponseEntity.ok(new ResultTrueDto());
    }
    return ResponseEntity.ok(new ResultFalseDto());
  }

  @Valid
  private ResultFalseWithErrorsDto checkPostsTitleAndText(AddPostDto addPostDto) {
    ResultFalseWithErrorsDto resultFalse = new ResultFalseWithErrorsDto();
    addPostDto.getText();
//    if (addPostDto.getText().length() < 15) {
//      resultFalse.addNewError("text", "Текст должен быть больше 15 символов");
//    }
//    if (addPostDto.getTitle().equals("")) {
//      resultFalse.addNewError("title", "Пустой заголовок :(");
//    }
    addPostDto.getTitle();
    return resultFalse;
  }

  private void canAddOrEditPost(User user) {
    boolean canAddPost = globalSettingService.getGlobalSetting().isMultiUserMode();
    if (!canAddPost) {
      if (user.getName().contains("MODERATOR")){
        throw new BadRequestException("Создать пост невозможно");
      }
//      if (user.getIsModerator() != 1) {
//        throw new BadRequestException("Создать пост невозможно");
//      }
    }
  }
}
