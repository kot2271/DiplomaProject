package blog.controller;

import blog.dto.requestDto.EmailDto;
import blog.dto.requestDto.LoginDto;
import blog.dto.requestDto.PasswordDto;
import blog.dto.requestDto.RegistrationDto;
import blog.dto.responseDto.*;
import blog.model.CaptchaCode;
import blog.model.User;
import blog.service.*;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth/")
@RequiredArgsConstructor
public class ApiAuthController {

  private final AuthService authService;
  private final UserService userService;
  private final PostService postService;
  private final CaptchaService captchaService;
  private final MailSender mailSender;


  @PostMapping("login")
  public Object login(@RequestBody LoginDto loginDto) {
    User userFromDB =
        userService.getUserByEmailAndPassword(loginDto.getEmail(), loginDto.getPassword());
    return getUserResponseEntity(userFromDB);
  }

  @GetMapping("check")
  public Object check() {
    Integer userId = authService.getUserIdOnSessionId();
    User userFromDB = userService.getUserById(userId);
    return getUserResponseEntity(userFromDB);
  }

  @GetMapping("logout")
  public ResultTrueDto logout() {
    authService.deleteSession();
    return new ResultTrueDto();

  }

  @GetMapping("captcha")
  public ResponseEntity<?> captcha() {
    CaptchaCode captchaCode = captchaService.generateCaptcha();
    String image = captchaService.getImageBase64(captchaCode.getCode(), 20);
    return ResponseEntity.ok(new CaptchaDto(captchaCode.getSecretCode(), image));
  }

  @PostMapping("register")
  public Object registration(@RequestBody RegistrationDto registrationDto) {
    ResultFalseWithErrorsDto resultFalse = preRegistrationVerification(registrationDto);
    if (resultFalse.getErrors().size() > 0) {
//      return ResponseEntity.ok(resultFalse);
      return resultFalse;
    }
    userService.registration(registrationDto);
//    return ResponseEntity.ok(new ResultTrueDto());
    return new ResultTrueDto();
  }

  @PostMapping("restore")
  public Object restore(@RequestBody EmailDto emailDto) {
    if (!userService.userExistByEmail(emailDto.getEmail())) {
//      return ResponseEntity.ok(new ResultFalseDto());
      return new ResultFalseDto();
    }
    mailSender.send(emailDto.getEmail(), "Restore password", "message");

//    return ResponseEntity.ok(new ResultTrueDto());
    return new ResultTrueDto();
  }

  @PostMapping("password")
  public Object password(@RequestBody PasswordDto passwordDto) {
    User user = userService.getUserByRestoreCode(passwordDto.getCode());
    ResultFalseWithErrorsDto resultFalse = preRestorePasswordVerification(passwordDto, user);
    if (resultFalse.getErrors().size() > 0) {
//      return ResponseEntity.ok(resultFalse);
      return resultFalse;
    }
    userService.restoreUserPassword(user, passwordDto.getPassword());
//    return ResponseEntity.ok(new ResultTrueDto());
    return new ResultTrueDto();
  }

  private Object getUserResponseEntity(User userFromDB) {
    if (userFromDB != null) {
      Integer countNewPosts = null;
      if (userFromDB.getIsModerator() == 1) {
        countNewPosts = postService.getCountPostsToModeration();
        return getAuthUserResponseEntity(userFromDB, true, true, countNewPosts);
      }
      return getAuthUserResponseEntity(userFromDB, false, false, countNewPosts);
    }
//    return ResponseEntity.ok(new ResultFalseDto());
    return new ResultFalseDto();
  }

  private ResponseEntity<ResultTrueDtoWithUser> getAuthUserResponseEntity(
      User userFromDB, boolean isModerator, boolean setting, Integer countNewPosts) {
    UserToLoginDto userFullInformation =
        new UserToLoginDto(
            userFromDB.getId(),
            userFromDB.getName(),
            userFromDB.getPhoto(),
            userFromDB.getEmail(),
            isModerator,
            countNewPosts,
            setting);
    authService.saveSession(userFromDB.getId());
    return ResponseEntity.ok(new ResultTrueDtoWithUser(userFullInformation));
  }

  private ResultFalseWithErrorsDto preRegistrationVerification(RegistrationDto registrationDto) {
    ResultFalseWithErrorsDto resultFalse = new ResultFalseWithErrorsDto();
    String captcha = captchaService.codeFromSecretCode(registrationDto.getCaptchaSecret());
    if (!captcha.equals(registrationDto.getCaptcha())) {
      resultFalse.addNewError("captcha", "Неверный ввод кода с картинки");
    }
    Pattern pattern = Pattern.compile("^[A-Za-z0-9_А-Яа-я]{2,16}$");
    Matcher matcher = pattern.matcher(registrationDto.getName());
    if (!matcher.matches()) {
      resultFalse.addNewError("name", "Неверное имя, допутимая длина - от 2х до 16-ти символов");
    }
    if (registrationDto.getPassword().length() < 6) {
      resultFalse.addNewError("password", "Пароль короче 6-ти символов");
    }
    if (userService.userExistByEmail(registrationDto.getEmail())) {
      resultFalse.addNewError("email", "Увы! такой e-mail уже зарегистрирован");
    }
    return resultFalse;
  }

  private ResultFalseWithErrorsDto preRestorePasswordVerification(
      PasswordDto passwordDto, User user) {
    ResultFalseWithErrorsDto resultFalse = new ResultFalseWithErrorsDto();
    String captcha = captchaService.codeFromSecretCode(passwordDto.getCaptchaSecret());
    if (user == null) {
      resultFalse.addNewError(
          "code",
          "Ссылка для восстановления пароля устарела.\n"
              + "<a href=/login/restore-password>Повторный запос ссылки</a>");
    }
    if (!captcha.equals(passwordDto.getCaptcha())) {
      resultFalse.addNewError("captcha", "Неверный ввод кода с картинки");
    }
    if (passwordDto.getPassword().length() < 6) {
      resultFalse.addNewError("password", "Пароль короче 6-ти символов");
    }
    return resultFalse;
  }
}
