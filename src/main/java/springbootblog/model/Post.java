package springbootblog.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String image;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(nullable = false)
    private LocalDateTime publishedAt;

    private int numLikes = 0;

    private int numComments = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User postUser;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("publishedAt DESC")
    private List<Comment> comments = new ArrayList<>();

    public Post() {
        this.publishedAt = LocalDateTime.now();
        this.numLikes = 0;
        this.numComments = 0;
    }

    public Post(String title, String content, String image, String slug, User postUser) {
        this();
        this.title = title;
        this.content = content;
        this.image = image;
        this.slug = slug;
        this.postUser = postUser;
    }

    // Método para dar 'Me gusta'
    public void like() {
        this.numLikes++;
    }

    // Método para añadir un comentario
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
        this.numComments = comments.size();
    }

    // Formateo de fecha y hora para las plantillas
    public String getFormattedDate() {
        if (publishedAt == null) return "";
        return publishedAt.format(DateTimeFormatter.ofPattern("d MMMM", Locale.ENGLISH)).toUpperCase();
    }

    public String getFormattedTime() {
        if (publishedAt == null) return "";
        return publishedAt.format(DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH));
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public int getNumComments() {
        return numComments;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }

    public User getPostUser() {
        return postUser;
    }

    public void setPostUser(User postUser) {
        this.postUser = postUser;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        if (comments != null) {
            this.numComments = comments.size();
        }
    }
}

