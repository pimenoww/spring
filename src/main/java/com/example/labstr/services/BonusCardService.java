package com.example.labstr.services;



import com.example.labstr.dao.BonusCardDao;
import com.example.labstr.models.BonusCard;
import com.example.labstr.models.BonusTransaction;
import com.example.labstr.models.TransactionType;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BonusCardService {

    private final BonusCardDao bonusCardDao;

    public BonusCardService(BonusCardDao bonusCardDao) {
        this.bonusCardDao = bonusCardDao;
    }

    public BonusCard findById(Long id) {
        return bonusCardDao.findById(id);
    }

    public BonusCard findByCardNumber(String cardNumber) {
        return bonusCardDao.findByCardNumber(cardNumber);
    }

    public List<BonusCard> getAllCards() {
        return bonusCardDao.findAll();
    }

    public void createCard(BonusCard bonusCard) {
        bonusCardDao.save(bonusCard);
    }

    public void updateCard(BonusCard bonusCard) {
        bonusCardDao.update(bonusCard);
    }

    public void deleteCard(BonusCard bonusCard) {
        bonusCardDao.delete(bonusCard);
    }

    public void addTransaction(BonusCard card, TransactionType type, double amount) {
        BonusTransaction transaction = new BonusTransaction(card, type, amount);
        if (type == TransactionType.CREDIT) {
            card.setBalance(card.getBalance() + amount);
        } else if (type == TransactionType.DEBIT) {
            card.setBalance(card.getBalance() - amount);
        }
        card.getTransactions().add(transaction);
        bonusCardDao.update(card);
    }

    public boolean isValidCardNumber(String cardNumber) {
        return cardNumber.matches("\\d{6}");
    }
}
