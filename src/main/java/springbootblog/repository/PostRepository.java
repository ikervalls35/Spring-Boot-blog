package springbootblog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import springbootblog.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findBySlug(String slug);

    boolean existsBySlug(String slug);

    // Obtener los 5 posts más recientes (findRecents)
    List<Post> findTop5ByOrderByPublishedAtDesc();

    // Obtener todos los posts ordenados por fecha descendente con paginación (findPaginados)
    Page<Post> findAllByOrderByPublishedAtDesc(Pageable pageable);

    // Buscar posts por término en título o contenido con paginación (findBuscadosPaginados)
    @Query("SELECT p FROM Post p WHERE LOWER(p.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY p.publishedAt DESC")
    Page<Post> findBuscadosPaginados(@Param("searchTerm") String searchTerm, Pageable pageable);
}

