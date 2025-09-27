-- ========================
-- Dummy data for currencies
-- ========================
INSERT INTO currencies (currency, currency_code, symbol) VALUES
('US Dollar', 'USD', '$'),
('Euro', 'EUR', '€'),
('Japanese Yen', 'JPY', '¥');

-- ========================
-- Dummy data for roles
-- ========================
INSERT INTO roles (name, is_default) VALUES
('USER', TRUE),
('ADMIN', FALSE);

-- ========================
-- Dummy data for users
-- ========================
INSERT INTO users (name, email, password, username, enabled, currency_id) VALUES
('John Doe', 'john.doe@example.com', 'password123', 'johndoe', TRUE, 1),
('Jane Smith', 'jane.smith@example.com', 'password123', 'janesmith', TRUE, 2),
('Admin User', 'admin@example.com', 'admin123', 'adminuser', TRUE, 1);

-- ========================
-- Dummy data for user_roles
-- ========================
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- John Doe -> ROLE_USER
(2, 1), -- Jane Smith -> ROLE_USER
(3, 1), -- Admin User -> ROLE_USER
(3, 2); -- Admin User -> ROLE_ADMIN

-- ========================
-- Dummy data for categories
-- ========================
INSERT INTO categories (name, description) VALUES
('Salary', 'Monthly salary income'),
('Food', 'Expenses for food and dining'),
('Shopping', 'Personal shopping expenses');

-- ========================
-- Dummy data for accounts
-- ========================
INSERT INTO accounts (name, description, amount, user_id, category_id, currency_id) VALUES
('John Checking', 'John\'s main checking account', 1500.00, 1, 1, 1),
('Jane Savings', 'Jane\'s savings account', 3000.00, 2, 1, 2),
('Admin Account', 'Admin user account', 5000.00, 3, 2, 1);

-- ========================
-- Dummy data for transaction types
-- ========================
INSERT INTO transaction_types (name, description) VALUES
('Income', 'Money coming in'),
('Expense', 'Money going out');

-- ========================
-- Dummy data for transactions
-- ========================
INSERT INTO transactions (name, description, amount, transaction_type_id, account_id, user_id, category_id) VALUES
('Salary Payment', 'Monthly salary for John', 2000.00, 1, 1, 1, 1),
('Grocery Shopping', 'Weekly groceries', 150.00, 2, 1, 1, 2),
('Online Shopping', 'Bought new shoes', 80.00, 2, 2, 2, 3),
('Admin Bonus', 'Admin yearly bonus', 1000.00, 1, 3, 3, 1);

-- ========================
-- Dummy data for OTPs
-- ========================
INSERT INTO otps (email, otp) VALUES
('john.doe@example.com', '123456'),
('jane.smith@example.com', '654321'),
('admin@example.com', '000000');
