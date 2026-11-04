package springbootblog;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // Ruta / (Portada)
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("activeTab", "index");
        return "index"; 
    }

    // Ruta /about
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("activeTab", "about");
        return "about"; 
    }

    // Ruta /contact
    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("activeTab", "contact");
        return "contact"; 
    }

    // Ruta /blog
    @GetMapping("/blog")
    public String blog(Model model) {
        model.addAttribute("activeTab", "blog");
        return "blog"; 
    }

    // Ruta /single_post (Vista detallada de un artículo)
    @GetMapping("/single_post")
    public String singlePost(Model model) {
        model.addAttribute("activeTab", "blog"); // El blog se activa también aquí
        return "single_post"; 
    }
}