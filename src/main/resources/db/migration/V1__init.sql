-- Initial schema for financial API

-- ========================
-- Step 1: Create tables without circular dependencies
-- ========================

-- Create roles first (no dependencies)
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create currencies WITHOUT user_id foreign key first
CREATE TABLE IF NOT EXISTS currencies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    currency VARCHAR(100) NOT NULL,
    currency_code VARCHAR(10) NOT NULL,
    symbol VARCHAR(10),
    user_id BIGINT,
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create users table (references currencies)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    avatar_url TEXT,
    bio TEXT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    currency_id BIGINT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_currency FOREIGN KEY (currency_id) REFERENCES currencies(id)
);

-- ========================
-- Step 2: Add foreign key constraint from currencies to users
-- ========================
ALTER TABLE currencies
ADD CONSTRAINT fk_currencies_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Add unique constraint for user-specific currency codes
ALTER TABLE currencies
ADD CONSTRAINT uk_currency_user_code UNIQUE (user_id, currency_code);

-- Create user_roles junction table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Create categories table
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    avatar_url TEXT,
    user_id BIGINT,
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_categories_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Add unique constraint for user-specific category names
ALTER TABLE categories
ADD CONSTRAINT uk_category_user_name UNIQUE (user_id, name);

-- Create accounts table
CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    amount DECIMAL(19,2) NOT NULL DEFAULT 0,
    user_id BIGINT,
    category_id BIGINT,
    currency_id BIGINT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_accounts_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_accounts_currency FOREIGN KEY (currency_id) REFERENCES currencies(id)
);

-- Create transaction_types table
CREATE TABLE IF NOT EXISTS transaction_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    user_id BIGINT,
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_type_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create transactions table
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    amount DECIMAL(19,2) NOT NULL DEFAULT 0,
    transaction_type_id BIGINT,
    account_id BIGINT,
    user_id BIGINT,
    category_id BIGINT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_transactions_type FOREIGN KEY (transaction_type_id) REFERENCES transaction_types(id),
    CONSTRAINT fk_transactions_account FOREIGN KEY (account_id) REFERENCES accounts(id),
    CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_transactions_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Create otp_codes table
CREATE TABLE IF NOT EXISTS otp_codes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    otp_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    attempts INT NOT NULL DEFAULT 0,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ========================
-- Insert default data
-- ========================

-- Insert default roles
INSERT INTO roles (name) VALUES
('ROLE_USER'),
('ROLE_ADMIN');

-- Insert default system currencies
INSERT INTO currencies (currency, currency_code, symbol, user_id, is_system) VALUES
('US Dollar', 'USD', '$', NULL, TRUE),
('Euro', 'EUR', '€', NULL, TRUE),
('British Pound', 'GBP', '£', NULL, TRUE),
('Japanese Yen', 'JPY', '¥', NULL, TRUE),
('Thai Baht', 'THB', '฿', NULL, TRUE);

-- Insert default system categories
INSERT INTO categories (name, description, user_id, is_system) VALUES
('Food & Dining', 'Restaurants, groceries, and food delivery', NULL, TRUE),
('Transportation', 'Public transit, gas, parking, and ride-sharing', NULL, TRUE),
('Shopping', 'Clothing, electronics, and general shopping', NULL, TRUE),
('Entertainment', 'Movies, games, concerts, and hobbies', NULL, TRUE),
('Bills & Utilities', 'Electricity, water, internet, and phone bills', NULL, TRUE),
('Healthcare', 'Medical expenses, insurance, and pharmacy', NULL, TRUE),
('Education', 'Tuition, books, and courses', NULL, TRUE),
('Travel', 'Flights, hotels, and vacation expenses', NULL, TRUE),
('Income', 'Salary, bonuses, and other income sources', NULL, TRUE),
('Savings', 'Emergency fund, investments, and savings accounts', NULL, TRUE);

-- Insert default transaction types
INSERT INTO transaction_types (name, description, user_id, is_system) VALUES
('INCOME', 'Money received', NULL, TRUE),
('EXPENSE', 'Money spent', NULL, TRUE),
('TRANSFER', 'Money transferred between accounts', NULL, TRUE);