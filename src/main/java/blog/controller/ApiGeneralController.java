package blog.controller;

import blog.dto.Blog;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    @GetMapping("/init")
    public Blog init() {
        return new Blog();
    }

    @GetMapping("/settings")
    public String settings(){
        return null;
    }

    @GetMapping("/tag")
    public String tag() {
        return null;
    }

}
