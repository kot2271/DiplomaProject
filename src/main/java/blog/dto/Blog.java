package blog.dto;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@Data
public class Blog {
    private static final String TITLE = "DevPub";
    private static final String SUB_TITLE = "Хаккерские лайфхаки";
    private static final String PHONE = "+7 999 888 77 55";
    private static final String EMAIL = "blog@develop.com";
    private static final String COPYRIGHT = "Aleksandr Nevskiy";
    private static final String COPYRIGHT_FROM = "2020";

    @Bean
    @Scope("singleton")
    public Blog blog(){
        return new Blog();
    }

}
