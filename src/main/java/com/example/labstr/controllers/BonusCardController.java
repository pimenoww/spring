package com.example.labstr.controllers;



import com.example.labstr.models.BonusCard;
import com.example.labstr.models.TransactionType;
import com.example.labstr.models.User;
import com.example.labstr.services.BonusCardService;
import com.example.labstr.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping("/bonus-cards")
public class BonusCardController {

    private final BonusCardService bonusCardService;
    private final UserService userService;

    public BonusCardController(BonusCardService bonusCardService, UserService userService) {
        this.bonusCardService = bonusCardService;
        this.userService = userService;
    }

    // Главная страница списка всех карт
    @GetMapping
    public String getAllCards(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("username", username);

        if (userService.isAdmin(username)) {
            model.addAttribute("cards", bonusCardService.getAllCards());
            return "bonus-cards/list"; // Шаблон для администратора
        } else {
            List<BonusCard> userCards = bonusCardService.getCardsByUser(username);
            model.addAttribute("cards", userCards);
            return "bonus-cards/user_cards"; // Шаблон для пользователя
        }
    }

    // Страница добавления новой карты
    @GetMapping("/new")
    public String showNewCardForm(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null || !userService.isAdmin(username)) {
            return "error/403";
        }
        model.addAttribute("bonusCard", new BonusCard());
        return "bonus-cards/new";
    }

    @PostMapping("/new")
    public String createCard(
            HttpSession session,
            @RequestParam String cardNumber,
            @RequestParam String ownerName,
            @RequestParam String username, // Логин пользователя, указанного администратором
            Model model) {
        String adminUsername = (String) session.getAttribute("username");
        if (adminUsername == null || !userService.isAdmin(adminUsername)) {
            return "error/403"; // Ограничить доступ для неадминистраторов
        }

        // Найти пользователя по логину, указанному администратором
        User user = userService.findByUsername(username);
        if (user == null) {
            model.addAttribute("error", "Пользователь с логином '" + username + "' не найден.");
            return "bonus-cards/new";
        }

        // Проверка валидности номера карты
        if (!bonusCardService.isValidCardNumber(cardNumber)) {
            model.addAttribute("error", "Номер карты должен содержать 6 цифр.");
            return "bonus-cards/new";
        }

        // Создание новой карты, привязанной к указанному пользователю
        BonusCard bonusCard = new BonusCard(cardNumber, ownerName, 0.0, user);
        bonusCardService.createCard(bonusCard);

        return "redirect:/bonus-cards";
    }

    @GetMapping("/{id}/edit")
    public String showEditCardForm(@PathVariable Long id, Model model) {
        BonusCard card = bonusCardService.findById(id);
        model.addAttribute("bonusCard", card);
        return "bonus-cards/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateCard(@PathVariable Long id, @ModelAttribute BonusCard bonusCard) {
        bonusCard.setId(id); // Установим ID вручную
        bonusCardService.updateCard(bonusCard);
        return "redirect:/bonus-cards";
    }

    @PostMapping("/{id}/delete")
    public String deleteCard(HttpSession session, @PathVariable Long id) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/auth/login";
        }

        BonusCard card = bonusCardService.findById(id);
        if (card == null || (!userService.isAdmin(username) && !card.getUser().getUsername().equals(username))) {
            return "error/403";
        }

        bonusCardService.deleteCard(card);
        return "redirect:/bonus-cards";
    }

    @GetMapping("/{id}")
    public String manageCard(@PathVariable Long id, Model model) {
        BonusCard card = bonusCardService.findById(id);
        model.addAttribute("bonusCard", card);
        return "bonus-cards/manage";
    }

    @PostMapping("/{id}/credit")
    public String creditBalance(@PathVariable Long id, @RequestParam double amount) {
        BonusCard card = bonusCardService.findById(id);
        bonusCardService.addTransaction(card, TransactionType.CREDIT, amount);
        return "redirect:/bonus-cards/" + id;
    }

    @PostMapping("/{id}/debit")
    public String debitBalance(@PathVariable Long id, @RequestParam double amount) {
        BonusCard card = bonusCardService.findById(id);
        if (card.getBalance() < amount) {
            throw new IllegalArgumentException("Недостаточно баланса для списания.");
        }
        bonusCardService.addTransaction(card, TransactionType.DEBIT, amount);
        return "redirect:/bonus-cards/" + id;
    }

    @PostMapping("/{id}/purchase")
    public String processPurchase(HttpSession session, @PathVariable Long id, @RequestParam double amount) {
        String username = (String) session.getAttribute("username");
        if (username == null || !userService.isUser(username)) {
            return "error/403";
        }
        BonusCard card = bonusCardService.findById(id);
        bonusCardService.addTransaction(card, TransactionType.CREDIT, amount);
        return "redirect:/bonus-cards/" + id;
    }

    @GetMapping("/{id}/report")
    public void downloadReport(@PathVariable Long id, HttpServletResponse response) throws IOException {
        BonusCard card = bonusCardService.findById(id);

        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=bonus_card_" + card.getCardNumber() + "_report.txt");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("Отчет по бонусной карте");
            writer.println("------------------------");
            writer.println("Номер карты: " + card.getCardNumber());
            writer.println("Владелец: " + card.getOwnerName());
            writer.println("Текущий баланс: " + card.getBalance());
        }
    }
}
