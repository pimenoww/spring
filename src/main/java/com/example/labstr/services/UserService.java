package com.example.labstr.services;

import com.example.labstr.dao.UserDao;
import com.example.labstr.models.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean isAdmin(String username) {
        User user = userDao.findByUsername(username);
        return user != null && user.getRole() == User.Role.ADMIN;
    }

    public boolean isUser(String username) {
        User user = userDao.findByUsername(username);
        return user != null && user.getRole() == User.Role.USER;
    }

    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public void save(User user) {
        userDao.save(user);
    }
}
