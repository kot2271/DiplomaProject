package blog.controller;

import blog.dto.InitEntityDto;
import blog.dto.requestDto.AddCommentToPostDto;
import blog.dto.requestDto.ModerationPostDto;
import blog.dto.responseDto.*;
import blog.exceptions.BadRequestException;
import blog.model.Post;
import blog.model.PostComment;
import blog.model.User;
import blog.service.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.thymeleaf.util.StringUtils.randomAlphanumeric;


@RestController
@RequiredArgsConstructor
public class ApiGeneralController {

    private final Tag2PostService tag2PostService;
    private final PostService postService;
    private final AuthService authService;
    private final UserService userService;
    private final GlobalSettingService globalSettingService;
    private final PostCommentService postCommentService;
    @Value("${upload.path}")
    private final String location;

    @GetMapping("/api/init")
   public ResponseEntity<?> init(){
        InitEntityDto initEntityDto = new InitEntityDto("DevPub", "Рассказы разработчиков", "+79998886655", "mail@mail.ru", "Skillbox", "2020");
        return ResponseEntity.ok(initEntityDto);
    }

    @GetMapping("/api/tag")
    public ResponseEntity<?> getTag(){
        List<TagDto> tagDtoList = tag2PostService.getTagsSortedByUsingInPost();
        return ResponseEntity.ok(new TagListDto(tagDtoList));
    }

    @GetMapping("/api/calendar")
    public ResponseEntity<?> calendar(@RequestParam(defaultValue = "2020") String year){
        return ResponseEntity.ok(postService.getPostsToCalendar(year));
    }

    @GetMapping("/api/settings")
    public ResponseEntity<?> settings(){
        GlobalSettingDto globalSettingDto = globalSettingService.getGlobalSetting();
        return ResponseEntity.ok(globalSettingDto);
    }

    @PutMapping("/api/settings")
    public ResponseEntity<?> putSettings(@RequestBody GlobalSettingDto globalSettingDto){
        Integer userId = authService.getUserIdOnSessionId();
        authService.checkAuth(userId);
        User userFromDB = userService.getUserById(userId);
        if (userFromDB.getIsModerator() == 1){
            globalSettingService.addNewSetting(globalSettingDto);
            return ResponseEntity.ok(globalSettingDto);
        }
        throw new BadRequestException("Не достаточно прав");
    }

    @GetMapping("/api/statistics/all")
    public ResponseEntity<?> getAllStatistics(){
        Integer postsCount = postService.getCountAllPosts();
        Integer viewsCount = postService.getPostsViewsCount();
        Integer likesCount = postService.getAllPostsLikesOrDislikesCount(1);
        Integer dislikesCount = postService.getAllPostsLikesOrDislikesCount(-1);
        LocalDateTime firstPublication = postService.getFirstPublicationFromAllPosts();
        return ResponseEntity.ok(new StatisticsDto(postsCount, likesCount, dislikesCount, viewsCount, firstPublication));
    }

    @GetMapping("/api/statistics/my")
    public ResponseEntity<?> getMyStatistics(){
        Integer userId = authService.getUserIdOnSessionId();
        authService.checkAuth(userId);
        Integer postsCount = postService.getCountUserPosts(userId);
        Integer viewsCount = postService.getMyPostsViewsCount(userId);
        Integer likesCount = postService.getMyLikesOrDislikesCount(userId, 1);
        Integer dislikesCount = postService.getMyLikesOrDislikesCount(userId, -1);
        LocalDateTime firstPublication = postService.getMyFirstPublication(userId);
        return ResponseEntity.ok(new StatisticsDto(postsCount, likesCount, dislikesCount, viewsCount, firstPublication));
    }

    @PostMapping("/api/moderation")
    @ResponseStatus(HttpStatus.OK)
    public void moderation(@RequestBody ModerationPostDto moderationPostDto){
        Integer userId = authService.getUserIdOnSessionId();
        authService.checkAuth(userId);
        User userFromDB = userService.getUserById(userId);
        if (userFromDB.getIsModerator() == 0){
            throw new BadRequestException("Модератор не авторизован");
        }
        postService.moderationPost(moderationPostDto, userId);
    }

    @SneakyThrows
    @GetMapping("/api/image")
    public ResponseEntity<?> image(@RequestParam(value = "image", required = false)MultipartFile file){
        Integer userId = authService.getUserIdOnSessionId();
        authService.checkAuth(userId);
        if (file.isEmpty()){
            throw new BadRequestException("Модератор не авторизован");
        }
        String type = Objects.requireNonNull(file.getContentType()).split("/")[1];
        String randomName = randomAlphanumeric(10);
        String generateDirs = randomAlphabetic(2).toLowerCase() + "/" + randomAlphabetic(2).toLowerCase() + "/" + randomAlphabetic(2).toLowerCase() + "/";
        File uploadFolder = new File(location + generateDirs);
        if (!uploadFolder.exists()){
            uploadFolder.mkdirs();
        }
        String path = location + generateDirs + randomName + "." + type;
        File dstImage = new File(path);
        userService.saveImage(200, file, dstImage, type);
        return ResponseEntity.ok("/" + location + generateDirs + randomName + "." + type);
    }

    @PostMapping("/api/comment")
    public ResponseEntity<?> commentToPost(@RequestBody AddCommentToPostDto addCommentToPostDto){
        Integer userId = authService.getUserIdOnSessionId();
        authService.checkAuth(userId);
        if (addCommentToPostDto.getText().length() < 6){
            ResultFalseWithErrorsDto resultFalseWithErrorsDto = new ResultFalseWithErrorsDto();
            resultFalseWithErrorsDto.addNewError("text", "Комментарий длиной не менее 6-ти символов");
            return ResponseEntity.ok(resultFalseWithErrorsDto);
        }
        Post post = checkPostForCommentIsPresent(addCommentToPostDto.getPostId());
        if (addCommentToPostDto.getParentId() != null) {
            checkParentIdForCommentIsPresent(addCommentToPostDto.getParentId(),addCommentToPostDto.getPostId());
        }
        User userFromDB = userService.getUserById(userId);
        Integer newCommentId = postCommentService.addNewCommentToPost(addCommentToPostDto, post, userFromDB);
        return ResponseEntity.ok(new SuccessAddCommentDto(newCommentId));
    }

    private Post checkPostForCommentIsPresent(Integer postId){
        if (postId == null) {
            throw new BadRequestException("Несуществующий пост");
        }
        Post post = postService.getPostFromPostId(postId);
        if (post == null) {
            throw new BadRequestException("Несуществующий пост");
        }
        return post;
    }

    private void checkParentIdForCommentIsPresent(Integer parentId, Integer postId){
        PostComment comment = postCommentService.getPostCommentByParentIdAndPostId(parentId, postId);
        if (comment == null){
            throw new BadRequestException("Несуществующий комментарий");
        }
    }
}
