CREATE TABLE IF NOT EXISTS users (
    id SERIAl PRIMARY KEY,
    login TEXT NOT NULL,
    password TEXT NOT NULL,
    name TEXT,
    isAdmin BOOLEAN DEFAULT FALSE,
    token TEXT NOT NULL
);
