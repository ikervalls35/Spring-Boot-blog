package springbootblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springbootblog.model.Comment;
import springbootblog.model.Post;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostOrderByPublishedAtDesc(Post post);
}

