-- =====================================================
-- Game Backend Schema (MySQL 8+)
-- InnoDB wird verwendet, um Transaktionen und Foreign Keys zu unterstützen
-- und Datenkonsistenz bei parallelen Zugriffen sicherzustellen.
-- =====================================================

DROP DATABASE IF EXISTS m295;
CREATE DATABASE m295
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE m295;

-- =====================================================
-- users (aktueller Spielstand)
-- =====================================================
CREATE TABLE users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,

  username VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(100) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,

  role ENUM('ADMIN', 'PLAYER') NOT NULL DEFAULT 'PLAYER',
  level INT NOT NULL DEFAULT 1,
  xp INT NOT NULL DEFAULT 0,
  coins INT NOT NULL DEFAULT 0,

  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT chk_users_level CHECK (level >= 1),
  CONSTRAINT chk_users_xp CHECK (xp >= 0),
  CONSTRAINT chk_users_coins CHECK (coins >= 0)
) ENGINE=InnoDB;

-- =====================================================
-- stats (History / Snapshots)
-- =====================================================
CREATE TABLE stats (
  stats_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,

  level INT NOT NULL,
  xp INT NOT NULL,
  coins INT NOT NULL,

  saved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_stats_user
    FOREIGN KEY (user_id) REFERENCES users(user_id)
    ON DELETE CASCADE,

  CONSTRAINT chk_stats_level CHECK (level >= 1),
  CONSTRAINT chk_stats_xp CHECK (xp >= 0),
  CONSTRAINT chk_stats_coins CHECK (coins >= 0)
) ENGINE=InnoDB;

CREATE INDEX idx_stats_user_id ON stats(user_id);

-- =====================================================
-- item (Item-Typ + globaler Stock)
-- =====================================================
CREATE TABLE item (
  item_id INT AUTO_INCREMENT PRIMARY KEY,

  code VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  description TEXT,

  rarity ENUM('COMMON','RARE','EPIC','LEGENDARY') NOT NULL,
  stock INT NOT NULL DEFAULT 0,

  CONSTRAINT chk_item_stock CHECK (stock >= 0)
) ENGINE=InnoDB;

-- =====================================================
-- user_item (Inventar / aktueller Besitz)
-- =====================================================
CREATE TABLE user_item (
  user_id INT NOT NULL,
  item_id INT NOT NULL,
  quantity INT NOT NULL DEFAULT 0,

  PRIMARY KEY (user_id, item_id),

  CONSTRAINT fk_user_item_user
    FOREIGN KEY (user_id) REFERENCES users(user_id)
    ON DELETE CASCADE,

  CONSTRAINT fk_user_item_item
    FOREIGN KEY (item_id) REFERENCES item(item_id)
    ON DELETE CASCADE,

  CONSTRAINT chk_user_item_qty CHECK (quantity >= 0)
) ENGINE=InnoDB;

CREATE INDEX idx_user_item_item_id ON user_item(item_id);

-- =====================================================
-- achievement
-- =====================================================
CREATE TABLE achievement (
  achievement_id INT AUTO_INCREMENT PRIMARY KEY,

  code VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  description TEXT
) ENGINE=InnoDB;

-- =====================================================
-- user_achievement (pro User nur einmal)
-- =====================================================
CREATE TABLE user_achievement (
  user_id INT NOT NULL,
  achievement_id INT NOT NULL,

  unlocked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (user_id, achievement_id),

  CONSTRAINT fk_user_achievement_user
    FOREIGN KEY (user_id) REFERENCES users(user_id)
    ON DELETE CASCADE,

  CONSTRAINT fk_user_achievement_achievement
    FOREIGN KEY (achievement_id) REFERENCES achievement(achievement_id)
    ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_user_achievement_achievement_id ON user_achievement(achievement_id);
INSERT INTO users (user_id, username, email, password_hash, role, level, xp, coins)
VALUES
  (1, 'player', 'player@test.ch', '{noop}player', 'PLAYER', 1, 0, 0),
  (2, 'admin',  'admin@test.ch',  '{noop}admin',  'ADMIN',  1, 0, 0);
INSERT INTO item (item_id, code, name, description, rarity, stock)
VALUES
  (1, 'IT01', 'Sword', 'Test Item', 'COMMON', 1);
INSERT INTO achievement (achievement_id, code, name, description)
VALUES
  (1, 'ACH1', 'First Kill', 'First achievement');

