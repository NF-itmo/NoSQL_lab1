CREATE TABLE IF NOT EXISTS Users (
  id SERIAL PRIMARY KEY,
  username VARCHAR(60) UNIQUE NOT NULL,
  password_hash VARCHAR(60) NOT NULL
);

CREATE TABLE IF NOT EXISTS Books (
  id INTEGER PRIMARY KEY,
  title TEXT NOT NULL CHECK length(trim(title)) > 0,
  author TEXT NOT NULL CHECK length(trim(author)) > 0,
  genre TEXT NOT NULL CHECK length(trim(genre)) > 0,
  description TEXT,
  available BOOLEAN NOT NULL,
  views BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS User_settings (
  username VARCHAR(60) PRIMARY KEY,
  genre VARCHAR(100) NOT NULL
);