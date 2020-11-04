package blog.service;

import blog.dto.requestDto.ProfileDto;
import blog.dto.requestDto.RegistrationDto;
import blog.model.User;
import blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.thymeleaf.util.StringUtils.randomAlphanumeric;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    @Value("${upload.path}")
    private String location;

    public User getUserByEmailAndPassword(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            boolean auth = new BCryptPasswordEncoder().matches(password, optionalUser.get().getPassword());
            if (auth) {
                return optionalUser.get();
            }
            return null;
        }
        return null;
    }

    public User getUserById(Integer userId){
        if (userId == null){
            return null;
        } else {
            return userRepository.findById(userId).get();
        }
    }

    public void registration(RegistrationDto registrationDto){
        User user = new User();
        user.setName(registrationDto.getName());
        String password = new BCryptPasswordEncoder().encode(registrationDto.getPassword());
        user.setPassword(password);
        user.setEmail(registrationDto.getEmail());
        user.setIsModerator((byte)0);
        user.setRegTime(LocalDateTime.now());
        userRepository.save(user);
    }

    public boolean userExistByEmail(String email){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.isPresent();
    }

    public String getUsersRestorePasswordCode(String email){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        String recoverCode = UUID.randomUUID().toString();
        User user = optionalUser.get();
        user.setCode(recoverCode);
        userRepository.save(user);
        return recoverCode;
    }

    public User getUserByRestoreCode(String code){
        Optional<User> user = userRepository.findByCode(code);
        return user.orElse(null);
    }


    public void restoreUserPassword(User user, String newPassword){
        String password = new BCryptPasswordEncoder().encode(newPassword);
        user.setPassword(password);
        user.setCode(null);
        userRepository.save(user);
    }

    @SneakyThrows
    public void editUserWithPhoto(MultipartFile file, ProfileDto dto, User user){
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        if (dto.getPassword() != null){
            String password = new BCryptPasswordEncoder().encode(dto.getPassword());
            user.setPassword(password);
        }
        String pathToOldPhoto = user.getPhoto();
        if (pathToOldPhoto != null){
            new File(pathToOldPhoto.replaceFirst("/", "")).delete();
        }
        String type = file.getContentType().split("/")[1];
        String generateDirs = randomAlphabetic(2).toLowerCase() + "/"
                + randomAlphabetic(2).toLowerCase() + "/"
                + randomAlphabetic(2).toLowerCase() + "/";
        String randomName = randomAlphanumeric(10);
        File uploadFolder = new File(location + generateDirs);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }
        String path = location + generateDirs + randomName + "." + type;
        File file1 = new File(path);
        saveImage(50, file, file1, type);
        user.setPhoto("/" + path);
        userRepository.save(user);
    }

    public void editUserWithoutPhoto(ProfileDto dto, User user){
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        if (dto.getPassword() != null){
            String password = new BCryptPasswordEncoder().encode(dto.getPassword());
            user.setPassword(password);
        }
        if (user.getPhoto() != null && dto.getRemovePhoto() == 1){
            new File(user.getPhoto().replaceFirst("/", "")).delete();
            user.setPhoto(null);
        }
        userRepository.save(user);
    }

    @SneakyThrows
    public void saveImage(Integer newWidth, MultipartFile image, File dstFile, String type) {
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(image.getBytes()));
        int newHeight = (int) Math.round(bufferedImage.getHeight() / (bufferedImage.getWidth() / (double) newWidth));
        BufferedImage newImage = Scalr.resize(bufferedImage, Scalr.Method.ULTRA_QUALITY, newWidth, newHeight);

        ImageIO.write(newImage, type, dstFile);
    }
}
