package springbootblog.model;

import jakarta.persistence.*;

@Entity
@Table(name = "images")
public class ImageItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String imageUrl; // Ruta o nombre del archivo de imagen
    private String section;  // Para saber si va al carrusel, galería, etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}