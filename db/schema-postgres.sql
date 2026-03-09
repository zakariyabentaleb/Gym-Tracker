-- PostgreSQL schema for Gym-Tracker (JWT auth)
-- Run this against your database (default: gymtracker)

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    roles VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS coaches (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    display_name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    bio TEXT,
    certifications TEXT,
    photo_url VARCHAR(1024),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_coach_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS courses (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  description VARCHAR(2000),
  duration_minutes INT,
  capacity INT,
  active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS course_schedules (
  id BIGSERIAL PRIMARY KEY,
  course_id BIGINT NOT NULL,
  coach_id BIGINT,
  room VARCHAR(100),
  start_time TIMESTAMP,
  end_time TIMESTAMP,
  capacity INT,
  active BOOLEAN DEFAULT TRUE,
  CONSTRAINT fk_schedule_course FOREIGN KEY (course_id) REFERENCES courses(id),
  CONSTRAINT fk_schedule_coach FOREIGN KEY (coach_id) REFERENCES coaches(id)
);

-- You can extend this file with the rest of your schema (members, bookings, payments, etc.)
