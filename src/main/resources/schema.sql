CREATE TABLE IF NOT EXISTS players (
    email VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255),
    name VARCHAR(255),
    surname VARCHAR(255),
    dob VARCHAR(255),
    address VARCHAR(255),
    time_limit BIGINT,
    session_start TIMESTAMP
    );