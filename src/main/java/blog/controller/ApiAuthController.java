package blog.controller;

import blog.dto.requestDto.EmailDto;
import blog.dto.requestDto.LoginDto;
import blog.dto.requestDto.PasswordDto;
import blog.dto.requestDto.RegistrationDto;
import blog.dto.responseDto.*;
import blog.model.CaptchaCode;
import blog.model.User;
import blog.repository.UserRepository;
import blog.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth/")
public class ApiAuthController {

    private final AuthService authService;
    private final UserService userService;
    private final PostService postService;
    private final CaptchaService captchaService;

    private final MailSender mailSender;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public ApiAuthController(AuthService authService, UserService userService, PostService postService, CaptchaService captchaService, AuthenticationManager authenticationManager, UserRepository userRepository, MailSender mailSender) {
        this.authService = authService;
        this.userService = userService;
        this.postService = postService;
        this.captchaService = captchaService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        Authentication auth =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        org.springframework.security.core.userdetails.User user =
                (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        return ResponseEntity.ok(getLoginResponse(user.getUsername()));

    }

    @GetMapping("check")
    public ResponseEntity<?> check(Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(new ResultTrueDtoWithUser());
        }
        return ResponseEntity.ok(getLoginResponse(principal.getName()));
    }

    @GetMapping("logout")
    public ResponseEntity<?> logout() {
        authService.deleteSession();
        return ResponseEntity.ok(new ResultTrueDto());
    }

    @GetMapping("captcha")
    public ResponseEntity<?> captcha() {
        CaptchaCode captchaCode = captchaService.generateCaptcha();
        String image = captchaService.getImageBase64(captchaCode.getCode(), 20);
        return ResponseEntity.ok(new CaptchaDto(captchaCode.getSecretCode(), image));
    }

    @PostMapping("register")
    public ResponseEntity<?> registration(@RequestBody RegistrationDto registrationDto) {
        ResultFalseWithErrorsDto resultFalse = preRegistrationVerification(registrationDto);
        if (resultFalse.getErrors().size() > 0) {
            return ResponseEntity.ok(resultFalse);
        }
        userService.registration(registrationDto);
        return ResponseEntity.ok(new ResultTrueDto());
    }

    @PostMapping("restore")
    public ResponseEntity<?> restore(@RequestBody EmailDto emailDto) {
        if (!userService.userExistByEmail(emailDto.getEmail())) {
            return ResponseEntity.ok(new ResultFalseDto());
        }
        String activationCode = userService.getUsersRestorePasswordCode(emailDto.getEmail());
        String url = "http://localhost:8080/login/change-password/" + activationCode;
        String message =
                "Hello! To recover your password, visit next link:"
                        + "<a href="
                        + url
                        + ">Восстановить пароль</a>";
        mailSender.send(emailDto.getEmail(), "Recover password", message);

        return ResponseEntity.ok(new ResultTrueDto());
    }

    @PostMapping("password")
    public ResponseEntity<?> password(@RequestBody PasswordDto passwordDto) {
        User user = userService.getUserByRestoreCode(passwordDto.getCode());
        ResultFalseWithErrorsDto resultFalse = preRestorePasswordVerification(passwordDto, user);
        if (resultFalse.getErrors().size() > 0) {
            return ResponseEntity.ok(resultFalse);
        }
        userService.restoreUserPassword(user, passwordDto.getPassword());
        return ResponseEntity.ok(new ResultTrueDto());
    }

    /**
     * приватные методы для различных превращений
     */

    private ResultTrueDtoWithUser getLoginResponse(String email) {
        User currentUser =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException(email));
        UserToLoginDto userToLoginDto = new UserToLoginDto();
        userToLoginDto.setEmail(currentUser.getEmail());
        userToLoginDto.setName(currentUser.getName());
        userToLoginDto.setModeration(currentUser.getIsModerator() == 1);
        userToLoginDto.setId(currentUser.getId());

        ResultTrueDtoWithUser resultTrueDtoWithUser = new ResultTrueDtoWithUser();
        resultTrueDtoWithUser.setResult(true);
        resultTrueDtoWithUser.setUser(userToLoginDto);
        return resultTrueDtoWithUser;
    }

    private ResponseEntity<?> getUserResponseEntity(User userFromDB) {
        if (userFromDB != null) {
            Integer countNewPosts = null;
            if (userFromDB.getIsModerator() == 1) {
                countNewPosts = postService.getCountPostsToModeration();
                return getAuthUserResponseEntity(userFromDB, true, true, countNewPosts);
            }
            return getAuthUserResponseEntity(userFromDB, false, false, countNewPosts);
        }
        return ResponseEntity.ok(new ResultFalseDto());
    }

    private ResponseEntity<ResultTrueDtoWithUser> getAuthUserResponseEntity(
            User userFromDB, boolean isModerator, boolean settings, Integer countNewPosts) {

        UserToLoginDto userFullInformation =
                new UserToLoginDto(
                        userFromDB.getId(),
                        userFromDB.getName(),
                        userFromDB.getPhoto(),
                        userFromDB.getEmail(),
                        isModerator,
                        countNewPosts,
                        settings);
        authService.saveSession(userFromDB.getId());

        return ResponseEntity.ok(new ResultTrueDtoWithUser(userFullInformation));
    }

    /**
     * проверка введенных значений при регистрации
     */
    private ResultFalseWithErrorsDto preRegistrationVerification(RegistrationDto registrationDto) {
        ResultFalseWithErrorsDto resultFalse = new ResultFalseWithErrorsDto();
        String captcha = captchaService.codeFromSecretCode(registrationDto.getCaptchaSecret());
        if (!captcha.equals(registrationDto.getCaptcha())) {
            resultFalse.addNewError("captcha", "Код с картинки введен неверно");
        }
        Pattern pattern = Pattern.compile("^[A-Za-z0-9_А-Яа-я]{2,16}$");
        Matcher matcher = pattern.matcher(registrationDto.getName());
        if (!matcher.matches()) {
            resultFalse.addNewError(
                    "name", "Имя указано неверно, доспустимая длина - от 2х до 16 символов");
        }
        if (registrationDto.getPassword().length() < 6) {
            resultFalse.addNewError("password", "Пароль короче 6-ти символов");
        }
        if (userService.userExistByEmail(registrationDto.getEmail())) {
            resultFalse.addNewError("email", "Этот e-mail уже зарегистрирован");
        }
        return resultFalse;
    }

    /**
     * проверка введенных значений при восстановлении пароля
     */
    private ResultFalseWithErrorsDto preRestorePasswordVerification(
            PasswordDto passwordDto, User user) {
        ResultFalseWithErrorsDto resultFalse = new ResultFalseWithErrorsDto();
        String captcha = captchaService.codeFromSecretCode(passwordDto.getCaptchaSecret());
        if (user == null) {
            resultFalse.addNewError(
                    "code",
                    "Ссылка для восстановления пароля устарела.\n"
                            + "<a href=/login/restore-password>Запросить ссылку снова</a>");
        }
        if (!captcha.equals(passwordDto.getCaptcha())) {
            resultFalse.addNewError("captcha", "Код с картинки введен неверно");
        }
        if (passwordDto.getPassword().length() < 6) {
            resultFalse.addNewError("password", "Пароль короче 6-ти символов");
        }
        return resultFalse;
    }
}
