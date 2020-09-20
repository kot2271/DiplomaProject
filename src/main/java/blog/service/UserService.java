package blog.service;

import blog.model.User;

import java.util.List;

public interface UserService {
    User getUser (int id);
    List<User> findAll();
}
