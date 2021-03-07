package blog.repository;

import blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * поиск юзера по имейлу
     */
    Optional<User> findByEmail(String email);

    /**
     * поиск юзера по коду для восстановления пароля
     */
    Optional<User> findByCode(String code);
}
