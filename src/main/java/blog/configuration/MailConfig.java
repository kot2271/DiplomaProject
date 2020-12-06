package blog.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class MailConfig {

  @Value("${spring.mail.host}")
  private String host;

  @Value("${spring.mail.username}")
  private String username;

  @Value("${spring.mail.password}")
  private String password;

  @Value("${spring.mail.port}")
  private String port;

  @Value("${spring.mail.properties.mail.transport.protocol}")
  private String protocol;

  @Value("${spring.mail.properties.mail.debug}")
  private String debug;

  @Bean
  public JavaMailSender getMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(host);
    mailSender.setPort(Integer.parseInt(port));
    mailSender.setUsername(username);
    mailSender.setPassword(password);
    Properties properties = mailSender.getJavaMailProperties();
    properties.setProperty("mail.transport.protocol", protocol);

    return mailSender;
  }
}
