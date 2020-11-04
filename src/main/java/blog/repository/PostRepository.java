package blog.repository;

import blog.model.Post;
import blog.model.enums.ModerationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {

  List<Post> findAllByModerationStatusAndIsActiveOrderByTimeDesc(
      ModerationStatus moderationStatus, Byte active, Pageable pageable);

  List<Post> findAllByModerationStatusAndIsActiveOrderByTimeAsc(
      ModerationStatus moderationStatus, Byte active, Pageable pageable);

  @Query(
      value =
          "select posts.* "
              + "from posts "
              + "left join post_comments "
              + "on posts.id = post_comments.post_id "
              + "where posts.is_active = 1 "
              + "and posts.moderation_status = 'ACCEPTED'"
              + "and posts.time <= now()"
              + "group by posts.id "
              + "order by count(post_comment.post_id) desc",
      nativeQuery = true)
  List<Post> mostPopularPosts(Pageable pageable);

  @Query(
      value =
          "SELECT posts.* "
              + "FROM posts "
              + "left join post_votes "
              + "on posts.id = post_votes.post_id "
              + "where posts.is_active = 1 and posts.moderation_status = 'ACCEPTED'"
              + "and posts.time <= now() and post_votes.value = 1 "
              + "group by posts.id "
              + "order by count(post_votes.post_id) desc",
      nativeQuery = true)
  List<Post> bestPosts(Pageable pageable);

  @Query(
      value =
          "SELECT count(*) FROM posts "
              + "WHERE is_active = 1 "
              + "AND moderation_status = :moderation_status "
              + "AND time <= now()",
      nativeQuery = true)
  Integer countAllPosts(@Param("moderation_status") String moderationStatus);

  Post getByIdAndModerationStatusAndIsActive(
      Integer id, ModerationStatus moderationStatus, Byte active);

  @Query(
      value =
          "SELECT posts.* FROM posts "
              + "LEFT JOIN tag2post "
              + "ON posts.id = tag2post.post_id "
              + "LEFT JOIN tags "
              + "ON tag2post.tag_id = tags.id "
              + "WHERE posts.is_active = 1 "
              + "AND posts.moderation_status = 'ACCEPTED'"
              + "AND posts.time <= now() "
              + "AND tags.name = :tag_name",
      nativeQuery = true)
  List<Post> getPostsByTag(@Param("tag_name") String tagName, Pageable pageable);

  @Query(
      value =
          "SELECT count(*) FROM posts "
              + "LEFT JOIN tag2post "
              + "ON posts.id = tag2post.post_id "
              + "LEFT JOIN tags "
              + "ON tag2post.tag_id = tags.id "
              + "WHERE posts.is_active = 1 "
              + "AND posts.moderation_status = 'ACCEPTED'"
              + "AND posts.time <= now() "
              + "AND tags.name = :tag_name",
      nativeQuery = true)
  Integer countPostsByTag(@Param("tag_name") String tagName);

  List<Post> findAllByTitleContainingAndIsActiveAndModerationStatusAndTimeIsBeforeOrderByTimeDesc(
      String title,
      Byte isActive,
      ModerationStatus moderationStatus,
      LocalDateTime time,
      Pageable pageable);

  Integer countAllByTitleContainingAndIsActiveAndModerationStatusAndTimeIsBeforeOrderByTimeDesc(
      String title, Byte isActive, ModerationStatus moderationStatus, LocalDateTime time);

  @Query(
      value =
          "SELECT * FROM posts "
              + "WHERE time LIKE :year "
              + "AND posts.is_active = 1 "
              + "AND posts.moderation_status = 'ACCEPTED'"
              + "AND posts.time <= now()",
      nativeQuery = true)
  List<Post> getPostsByYear(@Param("year") String year);

  @Query(
      value =
          "SELECT distinct year(time) FROM posts "
              + "WHERE posts.is_active = 1 "
              + "AND posts.moderation_status = 'ACCEPTED'"
              + "AND posts.time <= now()",
      nativeQuery = true)
  List<String> getAllPostsYears();

  @Query(
      value =
          "SELECT count(*) FROM posts "
              + "WHERE time LIKE :date "
              + "AND is_active = 1 "
              + "AND moderation_status = 'ACCEPTED'",
      nativeQuery = true)
  Integer countPostsByDate(@Param("date") String date);

  @Query(
      value =
          "SELECT posts.* FROM posts "
              + "WHERE time LIKE :date "
              + "AND is_active = 1 "
              + "AND moderation_status = 'ACCEPTED'",
      nativeQuery = true)
  List<Post> getPostByDate(@Param("date") String date, Pageable pageable);

  @Query(
      value =
          "SELECT * FROM posts "
              + "WHERE posts.user_id = :user_id "
              + "AND posts.is_active = 1 "
              + "AND posts.moderation_status = :moderation_status",
      nativeQuery = true)
  List<Post> getAllMyPosts(
      @Param("user_id") Integer userId,
      @Param("moderation_status") String moderationStatus,
      Pageable pageable);

  @Query(
      value =
          "SELECT count(*) FROM posts "
              + "WHERE user_id = :user_id "
              + "AND is_active = 1 "
              + "AND moderation_status = :moderation_status "
              + "AND time <= now()",
      nativeQuery = true)
  Integer countAllMyPosts(
      @Param("user_id") Integer userId, @Param("moderation_status") String moderationStatus);

  @Query(
      value =
          "SELECT * FROM posts " + "WHERE posts.user_id = :user_id " + "AND posts.is_active = 0 ",
      nativeQuery = true)
  List<Post> getAllMyInactivePosts(@Param("user_id") Integer userId);

  @Query(
      value =
          "SELECT count(*) FROM posts "
              + "WHERE posts.user_id = :user_id "
              + "AND posts.is_active = 0",
      nativeQuery = true)
  Integer countAllMyInactivePosts(@Param("user_id") Integer userId);

  @Query(
      value =
          "SELECT sum(view_count) FROM posts "
              + "WHERE is_active = 1 "
              + "AND moderation_status = 'ACCEPTED'"
              + "AND time <=()",
      nativeQuery = true)
  Integer postsViewsCount();

  @Query(value = "SELECT count(*) FROM post_votes " + "WHERE value = :value", nativeQuery = true)
  Integer allPostsLikesOrDislikesCount(@Param("value") Integer value);

  @Query(
      value =
          "SELECT time FROM posts "
              + "WHERE is_active = 1 "
              + "AND moderation_status = 'ACCEPTED'"
              + "AND time <= now() "
              + "ORDER BY time ASC LIMIT 1",
      nativeQuery = true)
  LocalDateTime firstPublicationFromAllPosts();

  @Query(
      value =
          "SELECT count(*) FROM post_votes "
              + "LEFT JOIN posts "
              + "ON post_votes.post_id = posts.id "
              + "WHERE posts.user_id = :user_id "
              + "AND posts.is_active = 1 "
              + "AND posts.moderation_status = 'ACCEPTED'"
              + "AND posts.time <= now() "
              + "AND post_votes.value = :value",
      nativeQuery = true)
  Integer myLikesOrDislikesCount(@Param("user_id") Integer userId, @Param("value") Integer value);

  @Query(
      value =
          "SELECT time FROM posts "
              + "WHERE is_active = 1 "
              + "AND user_id = :user_id "
              + "AND moderation_status = 'ACCEPTED'"
              + "AND time <= now() "
              + "ORDER BY time ASC LIMIT 1",
      nativeQuery = true)
  LocalDateTime myFirstPublication(@Param("user_id") Integer userId);

  @Query(
      value =
          "SELECT sum(view_count) FROM posts "
              + "WHERE is_active = 1 "
              + "AND moderation_status = 'ACCEPTED'"
              + "AND time <= now() "
              + "AND user_id = :user_id ",
      nativeQuery = true)
  Integer myPostsViewsCount(@Param("user_id") Integer userId);

  List<Post> findAllByIsActiveAndModerationStatusAndModeratorId(
      Byte active, ModerationStatus moderationStatus, Integer moderatorId, Pageable pageable);

  Integer countAllByModerationStatusAndIsActiveAndModeratorId(
      ModerationStatus moderationStatus, Byte active, Integer moderatorId);

  Post getPostById(Integer id);

  @Query(value = "SELECT user_id FROM posts " + "WHERE id = :id", nativeQuery = true)
  Integer getUserIdByPostId(@Param("id") Integer id);
}
