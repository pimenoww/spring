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
        model.addAttribute("cards", bonusCardService.getAllCards());
        return "bonus-cards/list";
    }

    // Страница добавления новой карты
    @GetMapping("/new")
    public String showNewCardForm(@RequestParam String username, Model model) {
        if (!userService.isAdmin(username)) {
            return "error/403";
        }
        model.addAttribute("bonusCard", new BonusCard());
        return "bonus-cards/new";
    }

    // Обработка создания новой карты
    @PostMapping("/new")
    public String createCard(@RequestParam String username, @ModelAttribute BonusCard bonusCard, Model model) {
        if (!userService.isAdmin(username)) {
            return "error/403";
        }
        if (!bonusCardService.isValidCardNumber(bonusCard.getCardNumber())) {
            model.addAttribute("error", "Номер карты должен содержать 6 цифр.");
            return "bonus-cards/new";
        }
        bonusCardService.createCard(bonusCard);
        return "redirect:/bonus-cards";
    }

    // Страница редактирования карты
    @GetMapping("/{id}/edit")
    public String showEditCardForm(@PathVariable Long id, Model model) {
        BonusCard card = bonusCardService.findById(id);
        model.addAttribute("bonusCard", card);
        return "bonus-cards/edit";
    }

    // Обработка редактирования карты
    @PostMapping("/{id}/edit")
    public String updateCard(@PathVariable Long id, @ModelAttribute BonusCard bonusCard) {
        bonusCard.setId(id); // Установим ID вручную
        bonusCardService.updateCard(bonusCard);
        return "redirect:/bonus-cards";
    }

    // Удаление карты
    @PostMapping("/{id}/delete")
    public String deleteCard(@PathVariable Long id) {
        BonusCard card = bonusCardService.findById(id);
        bonusCardService.deleteCard(card);
        return "redirect:/bonus-cards";
    }

    // Страница управления балансом
    @GetMapping("/{id}")
    public String manageCard(@PathVariable Long id, Model model) {
        BonusCard card = bonusCardService.findById(id);
        model.addAttribute("bonusCard", card);
        return "bonus-cards/manage";
    }

    // Добавление бонусов
    @PostMapping("/{id}/credit")
    public String creditBalance(@PathVariable Long id, @RequestParam double amount) {
        BonusCard card = bonusCardService.findById(id);
        bonusCardService.addTransaction(card, TransactionType.CREDIT, amount);
        return "redirect:/bonus-cards/" + id;
    }

    // Списание бонусов
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
    public String processPurchase(@RequestParam String username, @PathVariable Long id, @RequestParam double amount) {
        if (!userService.isUser(username)) {
            return "error/403";
        }
        BonusCard card = bonusCardService.findById(id);
        bonusCardService.addTransaction(card, TransactionType.CREDIT, amount);
        return "redirect:/bonus-cards/" + id;
    }

    // Скачивание отчета по карте
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
            writer.println();
            writer.println("История операций:");
            writer.println("Дата и время\t\tТип\t\tСумма");
            card.getTransactions().forEach(transaction -> writer.printf("%s\t%s\t%.2f%n",
                    transaction.getTransactionDate(),
                    transaction.getTransactionType(),
                    transaction.getAmount()));
        }
    }
}
