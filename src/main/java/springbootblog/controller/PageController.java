package springbootblog.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springbootblog.model.Comment;
import springbootblog.model.ContactMessage;
import springbootblog.model.Post;
import springbootblog.model.User;
import springbootblog.repository.CategoryRepository;
import springbootblog.repository.CommentRepository;
import springbootblog.repository.ContactMessageRepository;
import springbootblog.repository.ImageItemRepository;
import springbootblog.repository.PostRepository;
import springbootblog.repository.UserRepository;
import springbootblog.util.SlugUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
public class PageController {

    private static final int POSTS_PER_PAGE = 5;
    private static final String BLOG_UPLOAD_DIR = "src/main/resources/static/images/blog/";

    private final ContactMessageRepository contactMessageRepository;
    private final ImageItemRepository imageItemRepository;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public PageController(ContactMessageRepository contactMessageRepository,
                          ImageItemRepository imageItemRepository,
                          CategoryRepository categoryRepository,
                          PostRepository postRepository,
                          CommentRepository commentRepository,
                          UserRepository userRepository) {
        this.contactMessageRepository = contactMessageRepository;
        this.imageItemRepository = imageItemRepository;
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    // Ruta / (Portada) - Carga las imágenes del carrusel y las categorías desde la BD
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("activeTab", "index");
        model.addAttribute("carouselImages", imageItemRepository.findBySection("carousel"));
        model.addAttribute("categories", categoryRepository.findAllWithImages());
        return "index";
    }

    // Ruta /login (formulario personalizado de inicio de sesión)
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Página mostrada cuando un usuario autenticado no tiene permisos suficientes
    @GetMapping("/acceso-denegado")
    public String accessDenied() {
        return "access-denied";
    }

    // Ruta /about
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("activeTab", "about");
        return "about";
    }

    // Ruta /contact (Mostrar formulario)
    @GetMapping("/contact")
    public String contact(Model model) {
        if (!model.containsAttribute("contactMessage")) {
            model.addAttribute("contactMessage", new ContactMessage());
        }
        model.addAttribute("activeTab", "contact");
        return "contact";
    }

    // Ruta /contact (Procesar envío del formulario por POST)
    @PostMapping("/contact")
    public String handleContactForm(@Valid @ModelAttribute("contactMessage") ContactMessage contactMessage,
                                    BindingResult bindingResult,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeTab", "contact");
            return "contact";
        }

        contactMessageRepository.save(contactMessage);
        return "redirect:/contact/thankyou";
    }

    // Página de agradecimiento tras enviar el formulario de contacto
    @GetMapping("/contact/thankyou")
    public String contactThankYou(Model model) {
        model.addAttribute("activeTab", "contact");
        return "thankyou";
    }

    // ==========================================
    // SECCIÓN BLOG (TEORÍA CAPÍTULO 5)
    // ==========================================

    // 5.5 y 5.8: Listado de posts con paginación
    @GetMapping("/blog")
    public String blog(@RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        return showBlogPage(page, null, model);
    }

    @GetMapping("/blog/{page:[0-9]+}")
    public String blogPageNumber(@PathVariable("page") int page, Model model) {
        return showBlogPage(page, null, model);
    }

    // 5.11: Buscar entradas con término y paginación
    @GetMapping("/blog/buscar")
    public String blogBuscar(@RequestParam(value = "searchTerm", required = false, defaultValue = "") String searchTerm,
                             @RequestParam(value = "page", defaultValue = "1") int page,
                             Model model) {
        return showBlogPage(page, searchTerm, model);
    }

    private String showBlogPage(int page, String searchTerm, Model model) {
        int currentPage = Math.max(1, page);
        Pageable pageable = PageRequest.of(currentPage - 1, POSTS_PER_PAGE);

        Page<Post> postPage;
        boolean hasSearch = searchTerm != null && !searchTerm.trim().isEmpty();
        if (hasSearch) {
            postPage = postRepository.findBuscadosPaginados(searchTerm.trim(), pageable);
            model.addAttribute("searchTerm", searchTerm.trim());
        } else {
            postPage = postRepository.findAllByOrderByPublishedAtDesc(pageable);
            model.addAttribute("searchTerm", "");
        }

        int totalPages = Math.max(1, postPage.getTotalPages());
        if (currentPage > totalPages && postPage.getTotalElements() > 0) {
            if (hasSearch) {
                return "redirect:/blog/buscar?searchTerm=" + URLEncoder.encode(searchTerm, StandardCharsets.UTF_8) + "&page=" + totalPages;
            }
            return "redirect:/blog/" + totalPages;
        }

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("recents", postRepository.findTop5ByOrderByPublishedAtDesc());
        model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("total", postPage.getTotalElements());
        model.addAttribute("activeTab", "blog");

        return "blog";
    }

    // 5.2 y 5.3: Formulario para crear un nuevo post
    @GetMapping("/blog/new")
    public String newPost(Model model) {
        model.addAttribute("activeTab", "blog");
        return "new_post";
    }

    @PostMapping("/blog/new")
    public String handleNewPost(@RequestParam("title") String title,
                                @RequestParam("content") String content,
                                @RequestParam("file") MultipartFile file,
                                Principal principal,
                                Model model) {

        // Reto 5.3: Campos obligatorios
        if (title == null || title.isBlank() || content == null || content.isBlank() || file == null || file.isEmpty()) {
            model.addAttribute("error", "Los campos Título, Contenido e Imagen son obligatorios.");
            model.addAttribute("title", title);
            model.addAttribute("content", content);
            model.addAttribute("activeTab", "blog");
            return "new_post";
        }

        try {
            // Guardar físicamente la imagen en images/blog/
            Path uploadDir = Paths.get(BLOG_UPLOAD_DIR);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + extension;
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Copiar a target/classes si existe para disponibilidad inmediata en caliente
            Path targetBlogDir = Paths.get("target/classes/static/images/blog/");
            if (Files.exists(targetBlogDir)) {
                Files.copy(filePath, targetBlogDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            }

            // Generar slug SEO-friendly único
            String slug = SlugUtils.generateUniqueSlug(title, postRepository);

            // Obtener el usuario autenticado que crea el post
            User postUser = null;
            if (principal != null) {
                postUser = userRepository.findByUsername(principal.getName()).orElse(null);
            }

            Post post = new Post();
            post.setTitle(title.trim());
            post.setContent(content.trim());
            post.setImage(fileName);
            post.setSlug(slug);
            post.setPostUser(postUser);
            post.setNumLikes(0);
            post.setNumComments(0);
            post.setPublishedAt(LocalDateTime.now());

            postRepository.save(post);

            return "redirect:/single_post/" + post.getSlug();

        } catch (IOException e) {
            model.addAttribute("error", "Error al guardar la imagen: " + e.getMessage());
            model.addAttribute("activeTab", "blog");
            return "new_post";
        }
    }

    // 5.4: Ruta single_post/{slug} (Vista detallada de un artículo)
    @GetMapping("/single_post/{slug}")
    public String singlePost(@PathVariable("slug") String slug, Model model) {
        Optional<Post> optPost = postRepository.findBySlug(slug);
        if (optPost.isEmpty()) {
            model.addAttribute("post", null);
        } else {
            Post post = optPost.get();
            model.addAttribute("post", post);
            model.addAttribute("comments", commentRepository.findByPostOrderByPublishedAtDesc(post));
        }

        model.addAttribute("recents", postRepository.findTop5ByOrderByPublishedAtDesc());
        model.addAttribute("activeTab", "blog");
        return "single_post";
    }

    // Ruta de fallback si se accede a /single_post sin slug
    @GetMapping("/single_post")
    public String singlePostDefault() {
        List<Post> recents = postRepository.findTop5ByOrderByPublishedAtDesc();
        if (!recents.isEmpty()) {
            return "redirect:/single_post/" + recents.get(0).getSlug();
        }
        return "redirect:/blog";
    }

    // 5.7: Procesar nuevo comentario en un post
    @PostMapping("/single_post/{slug}/comment")
    public String addComment(@PathVariable("slug") String slug,
                             @RequestParam("name") String name,
                             @RequestParam("email") String email,
                             @RequestParam("text") String text,
                             Model model) {
        Optional<Post> optPost = postRepository.findBySlug(slug);
        if (optPost.isPresent()) {
            Post post = optPost.get();

            Comment comment = new Comment();
            comment.setName(name);
            comment.setEmail(email);
            comment.setText(text);
            comment.setPost(post);
            comment.setPublishedAt(LocalDateTime.now());

            commentRepository.save(comment);

            // Aumentamos en 1 el contador de comentarios del post
            post.setNumComments(post.getNumComments() + 1);
            postRepository.save(post);
        }
        return "redirect:/single_post/" + slug + "#comments-list";
    }

    // 5.9: Me gusta (AJAX con retorno JSON)
    @PostMapping("/single_post/{slug}/like")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> likeAjax(@PathVariable("slug") String slug) {
        Optional<Post> optPost = postRepository.findBySlug(slug);
        if (optPost.isPresent()) {
            Post post = optPost.get();
            post.like();
            postRepository.save(post);
            return ResponseEntity.ok(Map.of("success", true, "likes", post.getNumLikes()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false));
    }

    // Redirección de fallback para Me gusta si no se usa AJAX
    @GetMapping("/single_post/{slug}/like")
    public String likeFallback(@PathVariable("slug") String slug) {
        Optional<Post> optPost = postRepository.findBySlug(slug);
        if (optPost.isPresent()) {
            Post post = optPost.get();
            post.like();
            postRepository.save(post);
        }
        return "redirect:/single_post/" + slug;
    }
}
