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
    currency_id BIGINT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users(id),
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
    locked_until TIMESTAMP NULL,
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
('Thai Baht', 'THB', '฿', NULL, TRUE),
('United Arab Emirates Dirham', 'AED', 'د.إ', NULL, TRUE),
('Afghan Afghani', 'AFN', '؋', NULL, TRUE),
('Albanian Lek', 'ALL', 'L', NULL, TRUE),
('Armenian Dram', 'AMD', '֏', NULL, TRUE),
('Dutch Guilders', 'ANG', 'ƒ', NULL, TRUE),
('Angolan Kwanza', 'AOA', 'Kz', NULL, TRUE),
('Argentine Peso', 'ARS', '$', NULL, TRUE),
('Australian Dollar', 'AUD', '$', NULL, TRUE),
('Aruban Florin', 'AWG', 'ƒ', NULL, TRUE),
('Azerbaijani Manat', 'AZN', '₼', NULL, TRUE),
('Bosnia-Herzegovina Convertible Mark', 'BAM', 'KM', NULL, TRUE),
('Barbadian Dollar', 'BBD', '$', NULL, TRUE),
('Bangladeshi Taka', 'BDT', '৳', NULL, TRUE),
('Bulgarian Lev', 'BGN', 'лв', NULL, TRUE),
('Bahraini Dinar', 'BHD', '.د.ب', NULL, TRUE),
('Burundian Franc', 'BIF', 'Fr', NULL, TRUE),
('Bermudian Dollar', 'BMD', '$', NULL, TRUE),
('Bruneian Dollar', 'BND', '$', NULL, TRUE),
('Bolivian Boliviano', 'BOB', 'Bs.', NULL, TRUE),
('Brazilian Real', 'BRL', 'R$', NULL, TRUE),
('Brazilian PTAX', 'BRX', 'R$', NULL, TRUE),
('Bahamian Dollar', 'BSD', '$', NULL, TRUE),
('Bhutanese Ngultrum', 'BTN', 'Nu.', NULL, TRUE),
('Botswanan Pula', 'BWP', 'P', NULL, TRUE),
('Belarusian Ruble', 'BYN', 'Br', NULL, TRUE),
('Belizean Dollar', 'BZD', '$', NULL, TRUE),
('Canadian Dollar', 'CAD', '$', NULL, TRUE),
('Congolese Franc', 'CDF', 'Fr', NULL, TRUE),
('Swiss Franc', 'CHF', 'Fr', NULL, TRUE),
('Chilean Unit of Account UF', 'CLF', 'UF', NULL, TRUE),
('Chilean Peso', 'CLP', '$', NULL, TRUE),
('Chinese Yuan Offshore', 'CNH', '¥', NULL, TRUE),
('Chinese Yuan', 'CNY', '¥', NULL, TRUE),
('Colombian Peso', 'COP', '$', NULL, TRUE),
('Unidad de Valor Real (Colombia)', 'COU', 'UVR', NULL, TRUE),
('Costa Rican Colon', 'CRC', '₡', NULL, TRUE),
('Cuban Peso', 'CUP', '$', NULL, TRUE),
('Cape Verdean Escudo', 'CVE', '$', NULL, TRUE),
('Czech Republic Koruna', 'CZK', 'Kč', NULL, TRUE),
('Djiboutian Franc', 'DJF', 'Fr', NULL, TRUE),
('Danish Krone', 'DKK', 'kr', NULL, TRUE),
('Dominican Peso', 'DOP', '$', NULL, TRUE),
('Algerian Dinar', 'DZD', 'د.ج', NULL, TRUE),
('Egyptian Pound', 'EGP', '£', NULL, TRUE),
('Eritrean Nakfa', 'ERN', 'Nfk', NULL, TRUE),
('Ethiopian Birr', 'ETB', 'Br', NULL, TRUE),
('Fijian Dollar', 'FJD', '$', NULL, TRUE),
('Falkland Islands Pound', 'FKP', '£', NULL, TRUE),
('Georgian Lari', 'GEL', '₾', NULL, TRUE),
('Ghanaian Cedi', 'GHS', '₵', NULL, TRUE),
('Gibraltar Pound', 'GIP', '£', NULL, TRUE),
('Gambian Dalasi', 'GMD', 'D', NULL, TRUE),
('Guinean Franc', 'GNF', 'Fr', NULL, TRUE),
('Guatemalan Quetzal', 'GTQ', 'Q', NULL, TRUE),
('Guyanaese Dollar', 'GYD', '$', NULL, TRUE),
('Hong Kong Dollar', 'HKD', '$', NULL, TRUE),
('Honduran Lempira', 'HNL', 'L', NULL, TRUE),
('Croatian Kuna', 'HRK', 'kn', NULL, TRUE),
('Haitian Gourde', 'HTG', 'G', NULL, TRUE),
('Hungarian Forint', 'HUF', 'Ft', NULL, TRUE),
('Hungarian Forint Official Rate', 'HUX', 'Ft', NULL, TRUE),
('Indonesian Rupiah', 'IDR', 'Rp', NULL, TRUE),
('Israeli New Sheqel', 'ILS', '₪', NULL, TRUE),
('Indian Rupee', 'INR', '₹', NULL, TRUE),
('Iraqi Dinar', 'IQD', 'ع.د', NULL, TRUE),
('Iranian Rial', 'IRR', '﷼', NULL, TRUE),
('Icelandic Krona', 'ISK', 'kr', NULL, TRUE),
('Jamaican Dollar', 'JMD', '$', NULL, TRUE),
('Jordanian Dinar', 'JOD', 'د.ا', NULL, TRUE),
('Kenyan Shilling', 'KES', 'Sh', NULL, TRUE),
('Kyrgystani Som', 'KGS', 'с', NULL, TRUE),
('Cambodian Riel', 'KHR', '៛', NULL, TRUE),
('Comorian Franc', 'KMF', 'Fr', NULL, TRUE),
('North Korean Won', 'KPW', '₩', NULL, TRUE),
('South Korean Won', 'KRW', '₩', NULL, TRUE),
('Kuwaiti Dinar', 'KWD', 'د.ك', NULL, TRUE),
('Caymanian Dollar', 'KYD', '$', NULL, TRUE),
('Kazakhstani Tenge', 'KZT', '₸', NULL, TRUE),
('Laotian Kip', 'LAK', '₭', NULL, TRUE),
('Lebanese Pound', 'LBP', 'ل.ل', NULL, TRUE),
('Sri Lankan Rupee', 'LKR', 'Rs', NULL, TRUE),
('Liberian Dollar', 'LRD', '$', NULL, TRUE),
('Lesotho Maloti', 'LSL', 'L', NULL, TRUE),
('Libyan Dinar', 'LYD', 'ل.د', NULL, TRUE),
('Moroccan Dirham', 'MAD', 'د.م.', NULL, TRUE),
('Moldovan Leu', 'MDL', 'L', NULL, TRUE),
('Malagasy Ariary', 'MGA', 'Ar', NULL, TRUE),
('Macedonian Denar', 'MKD', 'ден', NULL, TRUE),
('Myanma Kyat', 'MMK', 'K', NULL, TRUE),
('Mongolian Tugrik', 'MNT', '₮', NULL, TRUE),
('Macanese Pataca', 'MOP', 'P', NULL, TRUE),
('Mauritanian Ouguiya', 'MRU', 'UM', NULL, TRUE),
('Mauritian Rupee', 'MUR', '₨', NULL, TRUE),
('Maldivian Rufiyaa', 'MVR', 'ރ.', NULL, TRUE),
('Malawian Kwacha', 'MWK', 'MK', NULL, TRUE),
('Mexican Peso', 'MXN', '$', NULL, TRUE),
('Mexican Unidad de Inversion', 'MXV', 'UDI', NULL, TRUE),
('Malaysian Ringgit', 'MYR', 'RM', NULL, TRUE),
('Mozambican Metical', 'MZN', 'MT', NULL, TRUE),
('Namibian Dollar', 'NAD', '$', NULL, TRUE),
('Nigerian Naira', 'NGN', '₦', NULL, TRUE),
('Nicaraguan Cordoba', 'NIO', 'C$', NULL, TRUE),
('Norwegian Krone', 'NOK', 'kr', NULL, TRUE),
('Nepalese Rupee', 'NPR', '₨', NULL, TRUE),
('New Zealand Dollar', 'NZD', '$', NULL, TRUE),
('Omani Rial', 'OMR', 'ر.ع.', NULL, TRUE),
('Panamanian Balboa', 'PAB', 'B/.', NULL, TRUE),
('Peruvian Nuevo Sol', 'PEN', 'S/', NULL, TRUE),
('Papua New Guinean Kina', 'PGK', 'K', NULL, TRUE),
('Philippine Peso', 'PHP', '₱', NULL, TRUE),
('Pakistani Rupee', 'PKR', '₨', NULL, TRUE),
('Polish Zloty', 'PLN', 'zł', NULL, TRUE),
('Paraguayan Guarani', 'PYG', '₲', NULL, TRUE),
('Qatari Rial', 'QAR', 'ر.ق', NULL, TRUE),
('Romanian Leu', 'RON', 'lei', NULL, TRUE),
('Serbian Dinar', 'RSD', 'дин', NULL, TRUE),
('Russian Ruble', 'RUB', '₽', NULL, TRUE),
('Rwandan Franc', 'RWF', 'Fr', NULL, TRUE),
('Saudi Arabian Riyal', 'SAR', 'ر.س', NULL, TRUE),
('Solomon Islands Dollar', 'SBD', '$', NULL, TRUE),
('Seychellois Rupee', 'SCR', '₨', NULL, TRUE),
('Sudanese Pound', 'SDG', '£', NULL, TRUE),
('Swedish Krona', 'SEK', 'kr', NULL, TRUE),
('Singapore Dollar', 'SGD', '$', NULL, TRUE),
('Saint Helena Pound', 'SHP', '£', NULL, TRUE),
('Sierra Leonean Leone', 'SLL', 'Le', NULL, TRUE),
('Somali Shilling', 'SOS', 'Sh', NULL, TRUE),
('Surinamese Dollar', 'SRD', '$', NULL, TRUE),
('South Sudanese Pound', 'SSP', '£', NULL, TRUE),
('Sao Tomean Dobra', 'STN', 'Db', NULL, TRUE),
('Salvadoran Colon', 'SVC', '₡', NULL, TRUE),
('Syrian Pound', 'SYP', '£', NULL, TRUE),
('Swazi Emalangeni', 'SZL', 'L', NULL, TRUE),
('Tajikistani Somoni', 'TJS', 'ЅМ', NULL, TRUE),
('Turkmenistani Manat', 'TMT', 'm', NULL, TRUE),
('Tunisian Dinar', 'TND', 'د.ت', NULL, TRUE),
('Tongan Pa''anga', 'TOP', 'T$', NULL, TRUE),
('Turkish Lira', 'TRY', '₺', NULL, TRUE),
('Trinidad and Tobago Dollar', 'TTD', '$', NULL, TRUE),
('Taiwan New Dollar', 'TWD', '$', NULL, TRUE),
('Tanzanian Shilling', 'TZS', 'Sh', NULL, TRUE),
('Ukrainian Hryvnia', 'UAH', '₴', NULL, TRUE),
('Ugandan Shilling', 'UGX', 'Sh', NULL, TRUE),
('Uruguayan Peso', 'UYU', '$', NULL, TRUE),
('Uzbekistan Som', 'UZS', "so'm", NULL, TRUE),
('Venezuelan Bolivar', 'VES', 'Bs.', NULL, TRUE),
('Vietnamese Dong', 'VND', '₫', NULL, TRUE),
('Ni-Vanuatu Vatu', 'VUV', 'Vt', NULL, TRUE),
('Samoan Tala', 'WST', 'T', NULL, TRUE),
('CFA Franc BEAC', 'XAF', 'Fr', NULL, TRUE),
('East Caribbean Dollar', 'XCD', '$', NULL, TRUE),
('Special Drawing Rights', 'XDR', 'SDR', NULL, TRUE),
('CFA Franc BCEAO', 'XOF', 'Fr', NULL, TRUE),
('CFP Franc', 'XPF', 'Fr', NULL, TRUE),
('Yemeni Rial', 'YER', '﷼', NULL, TRUE),
('South African Rand', 'ZAR', 'R', NULL, TRUE),
('Zambian Kwacha', 'ZMW', 'ZK', NULL, TRUE);

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