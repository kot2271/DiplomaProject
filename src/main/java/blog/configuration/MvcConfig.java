package blog.configuration;

import blog.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer, CommandLineRunner {

    @Value("${upload.path}")
    private String location;

    private final ImageService imageService;

    @Autowired
    public MvcConfig(ImageService imageService) {
        this.imageService = imageService;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(location + "/**/").addResourceLocations("file:" + location + "/");
    }

    @Override
    public void run(String... args) {
        imageService.init();
    }
}

