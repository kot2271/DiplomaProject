package generator;

import blog.model.User;
import lombok.SneakyThrows;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class UserGenerator {

    @SneakyThrows
    private static User createRandomUser() {
        List<String> names = List.of("John", "Claudia", "Mark", "Julia", "Brendon");

        byte isModerator;
        Date regTime;
        String name;
        String email;
        String password;
        String code = null;
        String photo = null;

        Random random = SecureRandom.getInstanceStrong();
        isModerator = (byte) random.nextInt(2);
        regTime = new Date();
        name = names.get(random.nextInt(names.size()));
        email = name + "@" + name + ".com";
        password = name.toLowerCase();

        return new User(isModerator, regTime, name, email, password, null, null);

    }

}
