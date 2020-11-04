package blog.service;

import blog.model.CaptchaCode;
import blog.repository.CaptchaRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class CaptchaService {
  private CaptchaRepository captchaRepository;

  public CaptchaCode generateCaptcha() {
    captchaRepository.deleteOld();
    CaptchaCode captchaCode = new CaptchaCode();
    int captchaMinNumbers = 1111;
    int captchaMaxNumbers = 9999;
    int randomCode = RandomUtils.nextInt(captchaMinNumbers, captchaMaxNumbers);
    captchaCode.setCode(Integer.toString(randomCode));
    captchaCode.setTime(LocalDateTime.now());
    int secretCodeLength = 22;
    captchaCode.setSecretCode(RandomStringUtils.randomAlphanumeric(secretCodeLength).toLowerCase());
    captchaRepository.save(captchaCode);
    return captchaCode;
  }

  public String getImageBase64(String code, int codeSize) {
    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();
    Font font = new Font("Times New Roman", Font.PLAIN, codeSize);
    g2d.setFont(font);
    FontMetrics fontMetrics = g2d.getFontMetrics();
    int width = fontMetrics.stringWidth(code);
    int height = fontMetrics.getHeight();
    g2d.dispose();

    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    g2d = image.createGraphics();

    g2d.setPaint(Color.RED);
    g2d.fillRect(0, 0, width, height);

    g2d.setRenderingHint(
        RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(
        RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
    g2d.setRenderingHint(
        RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    g2d.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

    g2d.setFont(font);
    fontMetrics = g2d.getFontMetrics();
    g2d.setColor(Color.WHITE);

    g2d.drawString(code, 0, fontMetrics.getAscent());
    g2d.dispose();
    String base64EncodeImage = "";

    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      ImageIO.write(image, "png", outputStream);
      base64EncodeImage =
          "data:image/png;charset=utf-8;base64, "
              + java.util.Base64.getEncoder().encodeToString(outputStream.toByteArray());

    } catch (IOException e) {
      e.printStackTrace();
    }
    return base64EncodeImage;
  }

  public String codeFromSecretCode(String secretCode) {
    CaptchaCode captcha = captchaRepository.findBySecretCode(secretCode);
    return captcha.getCode();
  }
}
