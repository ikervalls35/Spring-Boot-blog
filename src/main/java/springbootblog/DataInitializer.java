package springbootblog;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import springbootblog.model.Category;
import springbootblog.model.Comment;
import springbootblog.model.ImageItem;
import springbootblog.model.Post;
import springbootblog.model.User;
import springbootblog.repository.CategoryRepository;
import springbootblog.repository.CommentRepository;
import springbootblog.repository.ImageItemRepository;
import springbootblog.repository.PostRepository;
import springbootblog.repository.UserRepository;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      PostRepository postRepository,
                                      CommentRepository commentRepository,
                                      CategoryRepository categoryRepository,
                                      ImageItemRepository imageItemRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            // Usuario administrador
            User admin = userRepository.findByUsername("admin").orElseGet(() -> {
                User newAdmin = new User();
                newAdmin.setUsername("admin");
                newAdmin.setPassword(passwordEncoder.encode("1234"));
                newAdmin.setRole("ADMIN");
                return newAdmin;
            });
            boolean adminChanged = false;
            if (admin.getEmail() == null || admin.getEmail().isBlank()) {
                admin.setEmail("admin@photoblog.local");
                admin.setName("Administrador");
                adminChanged = true;
            }
            if (adminChanged) {
                admin = userRepository.save(admin);
            }

            // Categorías e imágenes de ejemplo para la portada y el panel de administración
            if (categoryRepository.count() == 0) {
                Category category1 = categoryRepository.save(new Category("Category I", "category-i"));
                Category category2 = categoryRepository.save(new Category("Category II", "category-ii"));
                Category category3 = categoryRepository.save(new Category("Category III", "category-iii"));

                for (int i = 1; i <= 4; i++) {
                    ImageItem image = new ImageItem();
                    image.setTitle("Portfolio " + i);
                    image.setSection("portfolio");
                    image.setImageUrl("/images/index/portfolio/" + i + ".jpg");
                    image.setCategory(category1);
                    imageItemRepository.save(image);
                }
                for (int i = 5; i <= 8; i++) {
                    ImageItem image = new ImageItem();
                    image.setTitle("Portfolio " + i);
                    image.setSection("portfolio");
                    image.setImageUrl("/images/index/portfolio/" + i + ".jpg");
                    image.setCategory(category2);
                    imageItemRepository.save(image);
                }
                for (int i = 9; i <= 12; i++) {
                    ImageItem image = new ImageItem();
                    image.setTitle("Portfolio " + i);
                    image.setSection("portfolio");
                    image.setImageUrl("/images/index/portfolio/" + i + ".jpg");
                    image.setCategory(category3);
                    imageItemRepository.save(image);
                }

                ImageItem carousel = new ImageItem();
                carousel.setTitle("Portada");
                carousel.setSection("carousel");
                carousel.setImageUrl("/images/index/woman.jpg");
                imageItemRepository.save(carousel);
            }

            // Si no hay posts en la BD, creamos posts de muestra para probar la paginación y el blog
            if (postRepository.count() == 0) {
                Post post1 = new Post();
                post1.setTitle("Doloremque illum");
                post1.setSlug("doloremque-illum");
                post1.setImage("landscape.jpg");
                post1.setContent("Duis ultrices tortor non felis convallis bibendum. Maecenas diam velit, sollicitudin at imperdiet ac, consectetur non nibh. Etiam eget dapibus nulla. Nam luctus nunc ut orci posuere ultricies. Donec id ante nec nunc laoreet vestibulum. Nulla in tortor non risus pharetra suscipit.");
                post1.setPostUser(admin);
                post1.setNumLikes(12);
                post1.setPublishedAt(LocalDateTime.now().minusDays(1));
                postRepository.save(post1);

                Comment c1 = new Comment("Young Papou", "papou@example.com", "¡Excelente fotografía y gran artículo! Me encantó la perspectiva.", post1);
                c1.setPublishedAt(LocalDateTime.now().minusHours(10));
                commentRepository.save(c1);

                Comment c2 = new Comment("Tom Junky", "tom@example.com", "Totalmente de acuerdo, los colores del paisaje son fascinantes.", post1);
                c2.setPublishedAt(LocalDateTime.now().minusHours(5));
                commentRepository.save(c2);

                post1.setNumComments(2);
                postRepository.save(post1);

                Post post2 = new Post();
                post2.setTitle("Lorem ipsum");
                post2.setSlug("lorem-ipsum");
                post2.setImage("family.jpg");
                post2.setContent("Nunc sit amet dapibus est, sit amet varius risus. Donec luctus lacinia mauris, at feugiat ligula facilisis ac. Class aptent taciti sociosqu ad litora torquent per conubia. Minima, earum fuga maiores unde quod quae aspernatur magnam quis adipisci ipsum maxime iusto quidem.");
                post2.setPostUser(admin);
                post2.setNumLikes(8);
                post2.setNumComments(0);
                post2.setPublishedAt(LocalDateTime.now().minusDays(2));
                postRepository.save(post2);

                Post post3 = new Post();
                post3.setTitle("Aliquam soluta");
                post3.setSlug("aliquam-soluta");
                post3.setImage("elephant.jpg");
                post3.setContent("In felis ante, aliquet sit amet venenatis at, feugiat sed leo. Fusce pretium, velit in luctus ornare, elit lorem ultrices tortor, sed consectetur orci risus mollis ante. Quod soluta corrupti earum officia vel inventore vitae quidem.");
                post3.setPostUser(admin);
                post3.setNumLikes(15);
                post3.setNumComments(0);
                post3.setPublishedAt(LocalDateTime.now().minusDays(3));
                postRepository.save(post3);

                Post post4 = new Post();
                post4.setTitle("Wedding Memories");
                post4.setSlug("wedding-memories");
                post4.setImage("wedding.jpg");
                post4.setContent("Curabitur id nisi ornare, viverra lorem sed, viverra ligula. Donec eu nibh tempus, semper velit sit amet, blandit odio. Phasellus id condimentum sapien, sit amet venenatis sem.");
                post4.setPostUser(admin);
                post4.setNumLikes(5);
                post4.setNumComments(0);
                post4.setPublishedAt(LocalDateTime.now().minusDays(4));
                postRepository.save(post4);

                Post post5 = new Post();
                post5.setTitle("Consequuntur");
                post5.setSlug("consequuntur");
                post5.setImage("woman.jpg");
                post5.setContent("Ipsa doloremque illum animi laborum quo in nemo delectus veritatis, amet numquam doloribus a iure sequi nobis vero facere necessitatibus ipsam. Recusandae dolore ipsam eius alias quidem.");
                post5.setPostUser(admin);
                post5.setNumLikes(20);
                post5.setNumComments(0);
                post5.setPublishedAt(LocalDateTime.now().minusDays(5));
                postRepository.save(post5);

                Post post6 = new Post();
                post6.setTitle("Nature & Wild Horizons");
                post6.setSlug("nature-wild-horizons");
                post6.setImage("landscape.jpg");
                post6.setContent("Explorando nuevos horizontes y capturando la belleza de la naturaleza silvestre. Una guía completa sobre iluminación natural para fotógrafos principiantes y avanzados.");
                post6.setPostUser(admin);
                post6.setNumLikes(7);
                post6.setNumComments(0);
                post6.setPublishedAt(LocalDateTime.now().minusDays(6));
                postRepository.save(post6);

                System.out.println(">>> Entradas de blog y comentarios de prueba inicializados correctamente.");
            }
        };
    }
}
