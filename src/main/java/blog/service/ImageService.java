package blog.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@Transactional
public class ImageService {
  @Value("${upload.path}")
  private String location;

  public void init() {
    try {
      Files.createDirectories(Paths.get(location));
    } catch (IOException e) {
      throw new RuntimeException("Не удалось инициализировать хранилище", e);
    }
  }
}
