package springbootblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springbootblog.model.Category;
import springbootblog.model.ImageItem;
import springbootblog.repository.CategoryRepository;
import springbootblog.repository.ImageItemRepository;
import springbootblog.util.SlugUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ImageItemRepository imageItemRepository;
    private final CategoryRepository categoryRepository;

    // Carpeta donde se guardarán físicamente las imágenes subidas
    private static final String UPLOAD_DIR = "src/main/resources/static/images/uploads/";

    public AdminController(ImageItemRepository imageItemRepository,
                           CategoryRepository categoryRepository) {
        this.imageItemRepository = imageItemRepository;
        this.categoryRepository = categoryRepository;
    }

    // Página principal del panel de administración
    @GetMapping("")
    public String adminDashboard(Model model) {
        model.addAttribute("images", imageItemRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
        return "admin/dashboard";
    }

    // Procesar la subida de una nueva imagen
    @PostMapping("/images/upload")
    public String uploadImage(@RequestParam("title") String title,
                              @RequestParam("section") String section,
                              @RequestParam(value = "categoryId", required = false) Long categoryId,
                              @RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                // Crear directorio de subidas si no existe
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Generar un nombre único para evitar colisiones
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath);

                // Guardar registro en la base de datos
                ImageItem imageItem = new ImageItem();
                imageItem.setTitle(title);
                imageItem.setSection(section);
                // Guardamos la ruta relativa para usarla luego con Thymeleaf
                imageItem.setImageUrl("/images/uploads/" + fileName);
                if (categoryId != null) {
                    categoryRepository.findById(categoryId).ifPresent(imageItem::setCategory);
                }

                imageItemRepository.save(imageItem);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/admin";
    }

    // Eliminar una imagen
    @GetMapping("/images/delete/{id}")
    public String deleteImage(@PathVariable("id") Long id) {
        imageItemRepository.deleteById(id);
        // Opcional: borrar el archivo físico del disco si se desea
        return "redirect:/admin";
    }

    // Crear una nueva categoría
    @PostMapping("/categories/add")
    public String addCategory(@RequestParam("name") String name) {
        String trimmed = name == null ? "" : name.trim();
        if (!trimmed.isEmpty() && !categoryRepository.existsByNameIgnoreCase(trimmed)) {
            String baseSlug = SlugUtils.toSlug(trimmed);
            String slug = baseSlug;
            int counter = 1;
            while (categoryRepository.existsBySlug(slug)) {
                slug = baseSlug + "-" + counter++;
            }
            categoryRepository.save(new Category(trimmed, slug));
        }
        return "redirect:/admin";
    }

    // Eliminar una categoría y desasociar sus imágenes
    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id) {
        categoryRepository.findById(id).ifPresent(category -> {
            imageItemRepository.findByCategory(category).forEach(image -> {
                image.setCategory(null);
                imageItemRepository.save(image);
            });
            categoryRepository.delete(category);
        });
        return "redirect:/admin";
    }
}
