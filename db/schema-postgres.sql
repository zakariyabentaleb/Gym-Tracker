-- PostgreSQL schema for Gym-Tracker (JWT auth)
-- Run this against your database (default: gymtracker)

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    roles VARCHAR(255) NOT NULL
);

-- Optional: index for fast lookups (unique already creates an index)
-- CREATE INDEX IF NOT EXISTS idx_users_username ON users (username);

