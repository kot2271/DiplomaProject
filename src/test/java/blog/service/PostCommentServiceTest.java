package blog.service;

import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import blog.dto.requestDto.AddCommentToPostDto;
import blog.model.Post;
import blog.model.PostComment;
import blog.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/data_test.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/clean.sql")
})
public class PostCommentServiceTest {
    @Autowired
    private PostCommentService postCommentService;

    @Test
    @SneakyThrows
    public void addNewCommentToPost() {
        AddCommentToPostDto addCommentToPostDto = new AddCommentToPostDto(1, 1, "test");
        Post post = new Post();
        post.setId(1);
        User user = new User();
        user.setId(1);
        Integer commentId = postCommentService.addNewCommentToPost(addCommentToPostDto, post, user);
        assertEquals(9, commentId);
    }

    @Test
    @SneakyThrows
    public void getPostCommentByParentIdAndPostId() {
        PostComment postComment = postCommentService.getPostCommentByParentIdAndPostId(1, 1);
        assertNull(postComment);
    }
}
