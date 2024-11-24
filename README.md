Система учёта бонусных карт

Есть разделение на ADMIN И USER

====================================================================================================
1. Что нужно для запуска?
   1) сгенеруйте в MYSQL базу данных
CREATE DATABASE bonus_prog;
USE bonus_prog;
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'USER') NOT NULL
);
CREATE TABLE bonus_card (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_number VARCHAR(50) NOT NULL UNIQUE,
    owner_name VARCHAR(100) NOT NULL,
    balance DOUBLE NOT NULL DEFAULT 0,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE TABLE bonus_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_number VARCHAR(50) NOT NULL,
    transaction_type ENUM('CREDIT', 'DEBIT') NOT NULL,
    amount DOUBLE NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (card_number) REFERENCES bonus_card(card_number)
); 
  2) зайдите в проекте в application.properties и введите свои логин и пароль от базы данных
  3) запустить приложение ;
2. Какая ссылка дял входа в приложение?
  - чтобы попасть в приложение можно воспользоваться 2 вариантами:
    1. если вы добавили пользователя и админа вручную при помощи скрипта в бд, то тогда используйте localhost:8081/auth/login
    2. если нет, то тогда localhost:8081/auth/register (пока что не совсем корректно работает)
====================================================================================================


Приятного пользования!


Best wishes by pimenoww
