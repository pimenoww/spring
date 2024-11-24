package com.example.labstr.controllers;

import com.example.labstr.models.User;
import com.example.labstr.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Страница входа
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    // Обработка входа
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        User user = userService.findByUsername(username);

        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error", "Неверный логин или пароль.");
            return "auth/login";
        }

        // Сохранение имени пользователя в сессии
        session.setAttribute("username", username);

        if (user.getRole() == User.Role.ADMIN) {
            return "redirect:/admin/menu";
        } else if (user.getRole() == User.Role.USER) {
            return "redirect:/user/menu";
        }

        model.addAttribute("error", "Неизвестная роль пользователя.");
        return "auth/login";
    }

    // Страница регистрации
    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    // Обработка регистрации
    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String role,
            Model model) {

        // Проверка, существует ли пользователь
        if (userService.findByUsername(username) != null) {
            model.addAttribute("error", "Пользователь с таким именем уже существует.");
            return "auth/register";
        }

        try {
            // Создаём нового пользователя
            User newUser = new User(username, password, User.Role.valueOf(role.toUpperCase()));
            userService.save(newUser); // Сохраняем пользователя в БД
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Неверная роль. Допустимые значения: ADMIN, USER.");
            return "auth/register";
        }

        // Перенаправляем на страницу входа после успешной регистрации
        return "redirect:/auth/login";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Очистка сессии
        return "redirect:/auth/login";
    }
}