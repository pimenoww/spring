package com.example.labstr.dao;

import com.example.labstr.models.BonusCard;
import java.util.List;

public interface BonusCardDao {
    BonusCard findById(Long id);
    BonusCard findByCardNumber(String cardNumber);
    List<BonusCard> findAll();
    void save(BonusCard bonusCard);
    void update(BonusCard bonusCard);
    void delete(BonusCard bonusCard);
}