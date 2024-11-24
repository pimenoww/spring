package com.example.labstr.dao;

//package com.example.bonusprogram.dao.impl;

import com.example.labstr.dao.BonusCardDao;
import com.example.labstr.models.BonusCard;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@Transactional
public class BonusCardDaoImpl implements BonusCardDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public BonusCard findById(Long id) {
        return entityManager.find(BonusCard.class, id);
    }

    @Override
    public BonusCard findByCardNumber(String cardNumber) {
        return entityManager.createQuery(
                        "SELECT b FROM BonusCard b WHERE b.cardNumber = :cardNumber", BonusCard.class)
                .setParameter("cardNumber", cardNumber)
                .getSingleResult();
    }

    @Override
    public List<BonusCard> findAll() {
        return entityManager.createQuery("FROM BonusCard", BonusCard.class).getResultList();
    }

    @Override
    public void save(BonusCard bonusCard) {
        entityManager.persist(bonusCard);
    }

    @Override
    public void update(BonusCard bonusCard) {
        entityManager.merge(bonusCard);
    }

    @Override
    public void delete(BonusCard bonusCard) {
        entityManager.remove(entityManager.contains(bonusCard) ? bonusCard : entityManager.merge(bonusCard));
    }
}
