package blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@ComponentScan({"com.blog"})

//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
//@SpringBootApplication(scanBasePackages={
//        "com.blog.controller", "com.blog.dto" , "com.blog.dto.requestDto", "com.blog.dto.responseDto", "com.blog.exceptions", "com.blog.configuration", "com.blog.model.enums", "com.blog.model", "com.blog.repository", "blog.service"})
@SpringBootApplication
@EntityScan(basePackages = "com.blog")
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}
