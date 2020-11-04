package blog.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class MailSender {
  @Autowired
  JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String username;

  @SneakyThrows
  public void send(String emailTo, String subject, String text) {
    MimeMessage mimeMessage = mailSender.createMimeMessage();

    MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
    message.setFrom(username);
    message.setTo(emailTo);
    message.setSubject(subject);
    message.setText(text, true);

    mailSender.send(mimeMessage);
  }
}
