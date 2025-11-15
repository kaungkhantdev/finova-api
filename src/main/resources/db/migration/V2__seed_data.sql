
-- ========================
-- Dummy data for users
-- ========================
INSERT INTO users (name, email, password, username, enabled, currency_id) VALUES
('John Doe', 'john.doe@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'johndoe', TRUE, 1), -- password: password123
('Jane Smith', 'jane.smith@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'janesmith', TRUE, 2), -- password: password123
('Admin User', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'adminuser', TRUE, 1); -- password: admin123

-- ========================
-- Dummy data for user_roles
-- ========================
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- John Doe -> USER
(2, 1), -- Jane Smith -> USER
(3, 1), -- Admin User -> USER
(3, 2); -- Admin User -> ADMIN


-- ========================
-- User-created custom categories
-- ========================
INSERT INTO categories (name, description, user_id, is_system) VALUES
('Freelance Income', 'Income from freelance projects', 1, FALSE), -- John's custom category
('Gym Membership', 'Monthly gym and fitness expenses', 1, FALSE), -- John's custom category
('Pet Care', 'Expenses for pet food and vet visits', 2, FALSE); -- Jane's custom category

-- ========================
-- User-created custom currencies
-- ========================
INSERT INTO currencies (currency, currency_code, symbol, user_id, is_system) VALUES
('Bitcoin', 'BTC', '₿', 1, FALSE), -- John's custom currency
('Canadian Dollar', 'CAD', 'C$', 2, FALSE); -- Jane's custom currency

-- ========================
-- Dummy data for accounts
-- ========================
INSERT INTO accounts (name, description, amount, user_id, currency_id) VALUES
('John Checking', 'John\'s main checking account', 1500.00, 1, 1), -- Uses system category (Salary) and system currency (USD)
('John Crypto Wallet', 'Bitcoin investment account', 0.5, 1, 6), -- Uses system category (Investments) and John's custom currency (BTC)
('Jane Savings', 'Jane\'s savings account', 3000.00, 2, 2), -- Uses system category (Salary) and system currency (EUR)
('Jane CAD Account', 'Canadian dollar account', 500.00, 2, 7), -- Uses system category and Jane's custom currency (CAD)
('Admin Account', 'Admin user account', 5000.00, 3, 1); -- Uses system category (Food & Dining) and system currency (USD)

-- ========================
-- Dummy data for transaction types
-- ========================
INSERT INTO transaction_types (name, description, user_id, is_system) VALUES
('Income', 'Money coming in', 1, FALSE),
('Expense', 'Money going out', 2, FALSE),
('Transfer', 'Money transferred between accounts', 1, FALSE);

-- ========================
-- Dummy data for transactions
-- ========================
INSERT INTO transactions (name, description, amount, transaction_type_id, account_id, user_id, category_id) VALUES
-- John's transactions
('Salary Payment', 'Monthly salary for John', 2000.00, 1, 1, 1, 1), -- System category: Salary
('Grocery Shopping', 'Weekly groceries', 150.00, 2, 1, 1, 2), -- System category: Food & Dining
('Freelance Project', 'Website development project', 500.00, 1, 1, 1, 11), -- John's custom category: Freelance Income
('Gym Monthly Fee', 'Monthly gym membership', 50.00, 2, 1, 1, 12), -- John's custom category: Gym Membership
('Bitcoin Purchase', 'Invested in crypto', 0.1, 1, 2, 1, 9), -- System category: Investments

-- Jane's transactions
('Online Shopping', 'Bought new shoes', 80.00, 2, 3, 2, 3), -- System category: Shopping
('Salary Deposit', 'Monthly salary', 2500.00, 1, 3, 2, 1), -- System category: Salary
('Pet Vet Visit', 'Annual checkup for dog', 120.00, 2, 3, 2, 13), -- Jane's custom category: Pet Care
('Transfer to CAD', 'Currency exchange', 200.00, 3, 4, 2, 1), -- Transfer to CAD account

-- Admin's transactions
('Admin Bonus', 'Admin yearly bonus', 1000.00, 1, 5, 3, 1), -- System category: Salary
('Restaurant Dinner', 'Team dinner expense', 200.00, 2, 5, 3, 2); -- System category: Food & Dining
