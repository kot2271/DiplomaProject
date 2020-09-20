package blog.controller;

import blog.dto.Blog;
import blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {


    @Autowired
    private PostRepository postRepository;


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
