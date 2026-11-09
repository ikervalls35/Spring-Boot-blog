package springbootblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springbootblog.model.Category;
import springbootblog.model.ImageItem;
import java.util.List;

public interface ImageItemRepository extends JpaRepository<ImageItem, Long> {
    // Para buscar imágenes filtradas por sección (ej: 'carousel')
    List<ImageItem> findBySection(String section);

    List<ImageItem> findByCategory(Category category);
}