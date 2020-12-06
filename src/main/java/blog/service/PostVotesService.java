package blog.service;

import blog.model.Post;
import blog.model.PostVote;
import blog.repository.PostVotesRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class PostVotesService {
    private PostVotesRepository postVotesRepository;

    public boolean takeLikeOrDislikeToPost(Post post, Integer userId, int likeOrDislike){
        Optional<PostVote> postVoteOptional = postVotesRepository.findByPostIdAndUserId(post.getId(), userId);

        if (postVoteOptional.isEmpty()){
            saveLikeOrDislike(post, userId, likeOrDislike);
            return true;
        }
        if (postVoteOptional.get().getValue() == likeOrDislike) {
            return false;
        }
        PostVote postVote = postVoteOptional.get();
        postVote.setValue((byte) likeOrDislike);
        postVote.setTime(LocalDateTime.now());
        postVotesRepository.save(postVote);
        return true;
    }

    private void saveLikeOrDislike (Post post, Integer userId, int likeOrDislike){
        PostVote postVote = new PostVote();
        postVote.setTime(LocalDateTime.now());
        postVote.setValue((byte) likeOrDislike);
        postVote.setPostId(post);
        postVote.setUserId(userId);
        postVotesRepository.save(postVote);
    }
}
