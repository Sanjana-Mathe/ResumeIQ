-- ══════════════════════════════════════════════
--  ResumeIQ Database Setup
--  Run this once in MySQL before starting the app
-- ══════════════════════════════════════════════

-- 1. Create the database
CREATE DATABASE IF NOT EXISTS resumeiq_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE resumeiq_db;

-- 2. Users table
CREATE TABLE IF NOT EXISTS users (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  name            VARCHAR(100)  NOT NULL,
  email           VARCHAR(255)  NOT NULL UNIQUE,
  password        VARCHAR(255)  NOT NULL,
  plan            ENUM('FREE','PREMIUM') NOT NULL DEFAULT 'FREE',
  analyses_count  INT           NOT NULL DEFAULT 0,
  chats_count     INT           NOT NULL DEFAULT 0,
  joined_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME      NULL,
  INDEX idx_email (email)
);

-- 3. Chat messages table
CREATE TABLE IF NOT EXISTS chat_messages (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id     BIGINT        NOT NULL,
  provider    ENUM('CLAUDE','META') NOT NULL,
  role        ENUM('USER','ASSISTANT') NOT NULL,
  content     TEXT          NOT NULL,
  created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_provider (user_id, provider)
);

-- 4. Resume analyses table
CREATE TABLE IF NOT EXISTS resume_analyses (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id          BIGINT        NOT NULL,
  ats_score        INT           NOT NULL,
  job_title        VARCHAR(255)  NULL,
  resume_text      TEXT          NULL,
  skills_json      JSON          NULL,
  keywords_json    JSON          NULL,
  suggestions_json JSON          NULL,
  careers_json     JSON          NULL,
  match_score      INT           NULL,
  created_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_created (user_id, created_at)
);

-- ══════════════════════════════════════════════
--  Verify tables were created
-- ══════════════════════════════════════════════
SHOW TABLES;
